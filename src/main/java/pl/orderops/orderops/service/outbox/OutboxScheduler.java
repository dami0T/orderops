package pl.orderops.orderops.service.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.orderops.orderops.model.outbox.OutboxEvent;
import pl.orderops.orderops.model.outbox.OutboxEventRepository;
import pl.orderops.orderops.model.outbox.OutboxEventStatus;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.model.webhook.WebhookEventRepository;
import pl.orderops.orderops.model.webhook.WebhookEventStatus;
import pl.orderops.orderops.service.action.ActionQueuePublisher;
import pl.orderops.orderops.service.action.ActionMessage;
import pl.orderops.orderops.service.rule.RuleEngine;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private static final int MAX_RETRIES = 5;

    private final OutboxEventRepository outboxEventRepository;
    private final WebhookEventRepository webhookEventRepository;
    private final ActionQueuePublisher actionQueuePublisher;
    private final RuleEngine ruleEngine;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${outbox.poll.interval:1000}")
    public void processOutbox() {
        OffsetDateTime now = OffsetDateTime.now();
        List<OutboxEvent> events = outboxEventRepository.findReadyToProcess(now);

        if (events.isEmpty()) {
            return;
        }

        log.info("Processing {} outbox events", events.size());

        for (OutboxEvent event : events) {
            try {
                processEventWithRetry(event);
            } catch (Exception e) {
                log.error("Failed to process outbox event id={}: {}", event.getId(), e.getMessage(), e);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processEventWithRetry(OutboxEvent event) throws Exception {
        try {
            outboxEventRepository.markAsProcessing(event.getId());
            processEvent(event);
            outboxEventRepository.markAsProcessed(event.getId(), OffsetDateTime.now(), OutboxEventStatus.PROCESSED);
            log.debug("Processed outbox event id={}", event.getId());
        } catch (Exception e) {
            handleFailure(event, e);
            throw e;
        }
    }

    private void processEvent(OutboxEvent event) throws Exception {
        switch (event.getAggregateType()) {
            case "WebhookEvent" -> {
                if ("WEBHOOK_RECEIVED".equals(event.getEventType())) {
                    handleWebhookReceived(event);
                }
            }
            case "ActionMessage" -> handleActionMessage(event);
            default -> log.warn("Unknown aggregateType={} in outbox id={}", 
                                event.getAggregateType(), event.getId());
        }
    }

    private void handleWebhookReceived(OutboxEvent event) throws Exception {
        JsonNode payload = objectMapper.readTree(event.getPayloadJson());
        Long eventId = payload.get("eventId").asLong();

        WebhookEvent webhookEvent = webhookEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("WebhookEvent not found: " + eventId));

        log.info("Processing WebhookEvent through RuleEngine: eventId={}", eventId);

        webhookEventRepository.updateStatus(eventId, WebhookEventStatus.MATCHED);

        try {
            ruleEngine.process(webhookEvent);
            webhookEventRepository.updateStatus(eventId, WebhookEventStatus.ACTIONS_SCHEDULED);
        } catch (Exception e) {
            webhookEventRepository.updateStatus(eventId, WebhookEventStatus.FAILED);
            log.error("RuleEngine processing failed for webhookEvent id={}", eventId, e);
            throw e;
        }
    }

    private void handleActionMessage(OutboxEvent event) throws Exception {
        try {
            ActionMessage message = objectMapper.readValue(event.getPayloadJson(), ActionMessage.class);
            actionQueuePublisher.publish(message);
            log.debug("Published ActionMessage to Redis: revisionId={}, actionIndex={}, eventId={}", 
                      message.getRuleRevisionId(), message.getActionIndex(), message.getEventId());
        } catch (Exception e) {
            log.error("Failed to deserialize/publish ActionMessage for outbox id={}", event.getId(), e);
            throw e;
        }
    }

    private void handleFailure(OutboxEvent event, Exception e) {
        int retryCount = event.getRetryCount() + 1;

        if (retryCount >= MAX_RETRIES) {
            log.error("Max retries exceeded for outbox event id={}, marking as processed with error", event.getId());
            outboxEventRepository.markAsProcessed(event.getId(), OffsetDateTime.now(), OutboxEventStatus.FAILED);
            return;
        }

        long delaySeconds = (long) Math.pow(2, retryCount);
        OffsetDateTime nextRetry = OffsetDateTime.now().plus(delaySeconds, ChronoUnit.SECONDS);

        outboxEventRepository.markForRetry(event.getId(), retryCount, nextRetry, e.getMessage(), OutboxEventStatus.RETRY);

        log.warn("Scheduled retry for outbox event id={}, attempt={}, delay={}s", 
                 event.getId(), retryCount, delaySeconds);
    }
}
