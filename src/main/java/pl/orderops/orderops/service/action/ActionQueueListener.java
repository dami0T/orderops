package pl.orderops.orderops.service.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.action.model.ActionExecution;
import pl.orderops.orderops.action.model.ActionExecutionRepository;
import pl.orderops.orderops.action.model.ActionStatus;
import pl.orderops.orderops.config.RedisConfig;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.model.webhook.WebhookEventRepository;
import pl.orderops.orderops.model.webhook.WebhookEventStatus;
import pl.orderops.orderops.ruleengine.model.RuleRevision;
import pl.orderops.orderops.ruleengine.model.RuleRevisionRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionQueueListener {

    private static final String CONSUMER_GROUP = "orderops-actions";
    private static final String CONSUMER_NAME = "action-worker-1";
    private static final String DLQ_STREAM = "orderops:actions-dlq";
    private static final int MAX_RETRIES = 5;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RuleRevisionRepository revisionRepository;
    private final WebhookEventRepository eventRepository;
    private final ActionExecutionRepository executionRepository;
    private final HttpActionExecutor httpExecutor;
    private final ActionMetrics metrics;
    private final ActionQueuePublisher publisher;
    private final DelayQueuePublisher delayPublisher;

    @PostConstruct
    public void startListener() {
        try {
            redisTemplate.opsForStream().createGroup(RedisConfig.ACTION_STREAM, ReadOffset.from("0"), CONSUMER_GROUP);
            log.info("Created consumer group: {}", CONSUMER_GROUP);
        } catch (Exception e) {
            log.warn("Consumer group already exists or stream not found: {}", e.getMessage());
        }

        Thread listenerThread = new Thread(this::listen, "redis-action-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
        log.info("Started Redis action queue listener thread");
    }

    public void listen() {
        log.info("Starting action queue listener...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().read(
                        Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
                        StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                        StreamOffset.create(RedisConfig.ACTION_STREAM, ReadOffset.lastConsumed())
                );

                if (messages != null && !messages.isEmpty()) {
                    for (MapRecord<String, Object, Object> record : messages) {
                        processMessage(record);
                        redisTemplate.opsForStream().acknowledge(RedisConfig.ACTION_STREAM, CONSUMER_GROUP, record.getId());
                    }
                }
            } catch (Exception e) {
                log.error("Error processing queue message", e);
            }
        }
    }

    private void processMessage(MapRecord<String, Object, Object> record) {
        ActionExecution execution = null;
        
        try {
            Map<Object, Object> data = record.getValue();
            String json = (String) data.get("value");
            
            ActionMessage message = objectMapper.readValue(json, ActionMessage.class);
            log.info("Processing action: revisionId={}, eventId={}, attempt={}",
                    message.getRuleRevisionId(), message.getEventId(), message.getAttempt());

            RuleRevision revision = revisionRepository
                    .findById(message.getRuleRevisionId())
                    .orElseThrow(() -> new RuntimeException("Revision not found: " + message.getRuleRevisionId()));

            WebhookEvent event = eventRepository
                    .findById(message.getEventId())
                    .orElseThrow(() -> new RuntimeException("Event not found: " + message.getEventId()));

            execution = createExecution(message);
            
            Map<String, Object> action = extractAction(revision, message.getActionIndex());
            executeAction(action, event);

            execution.setStatus(ActionStatus.SUCCESS);
            execution.setCompletedAt(OffsetDateTime.now());
            executionRepository.save(execution);

            metrics.incrementSucceeded();
            log.info("Action executed successfully: revisionId={}, eventId={}",
                    message.getRuleRevisionId(), message.getEventId());

            checkAndUpdateEventStatus(event, revision);

        } catch (Exception e) {
            log.error("Failed to process message: {}", e.getMessage(), e);
            if (execution != null) {
                execution.setStatus(ActionStatus.FAILED);
                execution.setLastError(e.getMessage());
                execution.setAttempts(execution.getAttempts() + 1);
                executionRepository.save(execution);
            }
            handleFailure(record, e);
        }
    }

    private ActionExecution createExecution(ActionMessage message) {
        ActionExecution execution = ActionExecution.builder()
                .ruleRevisionId(message.getRuleRevisionId())
                .actionIndex(message.getActionIndex())
                .eventId(message.getEventId())
                .status(ActionStatus.PENDING)
                .attempts(message.getAttempt())
                .createdAt(OffsetDateTime.now())
                .build();
        return executionRepository.save(execution);
    }

    private void checkAndUpdateEventStatus(WebhookEvent event, RuleRevision revision) {
        try {
            List<ActionExecution> executions = executionRepository.findByEventId(event.getId());
            
            List<Map<String, Object>> actions = objectMapper.readValue(revision.getActionsJson(), List.class);
            int totalActions = actions.size();
            long successCount = executions.stream()
                    .filter(e -> e.getStatus() == ActionStatus.SUCCESS)
                    .count();
            
            if (successCount >= totalActions) {
                event.setStatus(WebhookEventStatus.PROCESSED);
                event.setProcessedAt(OffsetDateTime.now());
                eventRepository.save(event);
                log.info("All actions completed for eventId={}, status=PROCESSED", event.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to check event status: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractAction(RuleRevision revision, int index) throws Exception {
        List<Map<String, Object>> actions = objectMapper.readValue(revision.getActionsJson(), List.class);
        if (index >= actions.size()) {
            throw new IllegalStateException("Action index out of range");
        }
        return actions.get(index);
    }

    private void executeAction(Map<String, Object> action, WebhookEvent event) throws Exception {
        String type = (String) action.get("type");
        if (!"HTTP".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Unsupported action type: " + type);
        }
        httpExecutor.executeSync(objectMapper.writeValueAsString(action), event.getPayloadJson());
    }

    private void handleFailure(MapRecord<String, Object, Object> record, Exception e) {
        try {
            Map<Object, Object> data = record.getValue();
            String json = (String) data.get("value");
            ActionMessage message = objectMapper.readValue(json, ActionMessage.class);

            int nextAttempt = message.getAttempt() + 1;
            
            if (nextAttempt >= MAX_RETRIES) {
                metrics.incrementFailed();
                markEventAsFailed(message.getEventId());
                sendToDlq(record, message, e);
                log.error("Max retries exceeded for action: revisionId={}, eventId={}, sent to DLQ",
                        message.getRuleRevisionId(), message.getEventId());
                return;
            }

            metrics.incrementRetried();
            message.setAttempt(nextAttempt);
            
            long delaySeconds = (long) Math.pow(2, nextAttempt);
            delayPublisher.publishDelayed(message, delaySeconds);

            log.info("Scheduled retry for action: revisionId={}, attempt={}, delay={}s",
                    message.getRuleRevisionId(), nextAttempt, delaySeconds);

        } catch (Exception requeueEx) {
            log.error("Failed to schedule retry: {}", requeueEx.getMessage());
            metrics.incrementFailed();
        }
    }

    private void markEventAsFailed(Long eventId) {
        try {
            eventRepository.findById(eventId).ifPresent(event -> {
                event.setStatus(WebhookEventStatus.FAILED);
                event.setProcessedAt(OffsetDateTime.now());
                eventRepository.save(event);
                log.warn("Event marked as FAILED: eventId={}", eventId);
            });
        } catch (Exception e) {
            log.error("Failed to mark event as failed: {}", e.getMessage());
        }
    }

    private void sendToDlq(MapRecord<String, Object, Object> record, ActionMessage message, Exception error) {
        try {
            Map<String, String> dlqData = new HashMap<>();
            dlqData.put("originalMessage", objectMapper.writeValueAsString(message));
            dlqData.put("errorMessage", error.getMessage());
            dlqData.put("errorClass", error.getClass().getName());
            dlqData.put("failedAt", OffsetDateTime.now().toString());

            var dlqRecord = StreamRecords.newRecord()
                    .in(DLQ_STREAM)
                    .ofMap(dlqData);

            redisTemplate.opsForStream().add(dlqRecord);
            log.warn("Message sent to DLQ: revisionId={}, eventId={}", 
                    message.getRuleRevisionId(), message.getEventId());
        } catch (Exception dlqEx) {
            log.error("Failed to send message to DLQ: {}", dlqEx.getMessage());
        }
    }
}
