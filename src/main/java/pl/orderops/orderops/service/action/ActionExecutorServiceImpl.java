package pl.orderops.orderops.service.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.orderops.orderops.model.outbox.OutboxEvent;
import pl.orderops.orderops.model.outbox.OutboxEventRepository;
import pl.orderops.orderops.model.outbox.OutboxEventStatus;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.ruleengine.model.RuntimeRule;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionExecutorServiceImpl implements ActionExecutorService {

    private final OutboxEventRepository outboxEventRepository;
    private final ActionMessageSerializer serializer;

    @Override
    @Transactional
    public void queueExecution(Long revisionId, int actionIndex, Long eventId, String eventPayload) {
        ActionMessage message = ActionMessage.builder()
                .ruleRevisionId(revisionId)
                .actionIndex(actionIndex)
                .eventId(eventId)
                .eventPayload(eventPayload)
                .attempt(1)
                .build();

        saveToOutbox(message);
        log.info("Queued action (to outbox): revisionId={}, eventId={}", revisionId, eventId);
    }

    @Override
    public void queueExecution(RuntimeRule rule, WebhookEvent event) {
        for (int i = 0; i < rule.getActions().size(); i++) {
            queueExecution(rule.getRevisionId(), i, event.getId(), event.getPayloadJson());
        }
    }

    private void saveToOutbox(ActionMessage message) {
        try {
            String payload = serializer.serialize(message);
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType("ActionMessage")
                    .aggregateId(message.getEventId())
                    .eventType("ACTION_QUEUED")
                    .payloadJson(payload)
                    .createdAt(OffsetDateTime.now())
                    .status(OutboxEventStatus.PENDING)
                    .build();
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ActionMessage", e);
        }
    }
}
