package pl.orderops.orderops.service.action;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.ruleengine.model.RuntimeRule;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ActionExecutorServiceImplTest {

    @Mock
    private ActionQueuePublisher publisher;

    private ActionExecutorServiceImpl service;

    @Before
    public void setUp() {
        service = new ActionExecutorServiceImpl(publisher);
    }

    @Test
    void queueExecution_with_params_publishes_message() {
        Long revisionId = 1L;
        int actionIndex = 0;
        Long eventId = 100L;
        String payload = "{\"test\": \"data\"}";

        service.queueExecution(revisionId, actionIndex, eventId, payload);

        ArgumentCaptor<ActionMessage> captor = ArgumentCaptor.forClass(ActionMessage.class);
        verify(publisher).publish(captor.capture());

        ActionMessage message = captor.getValue();
        assertEquals(revisionId, message.getRuleRevisionId());
        assertEquals(actionIndex, message.getActionIndex());
        assertEquals(eventId, message.getEventId());
        assertEquals(payload, message.getEventPayload());
        assertEquals(1, message.getAttempt());
    }

    @Test
    void queueExecution_with_rule_and_event_publishes_for_each_action() {
        RuntimeRule rule = new RuntimeRule(
                1L,
                10L,
                "order.created",
                null,
                List.of(
                        Map.of("type", "HTTP", "url", "http://example.com/1"),
                        Map.of("type", "HTTP", "url", "http://example.com/2"),
                        Map.of("type", "HTTP", "url", "http://example.com/3")
                )
        );

        WebhookEvent event = WebhookEvent.builder()
                .id(100L)
                .tenantId(10L)
                .eventType("order.created")
                .payloadJson("{\"orderId\": \"123\"}")
                .build();

        service.queueExecution(rule, event);

        verify(publisher, times(3)).publish(any(ActionMessage.class));
    }

    @Test
    void queueExecution_with_empty_actions_publishes_nothing() {
        RuntimeRule rule = new RuntimeRule(
                1L,
                10L,
                "order.created",
                null,
                List.of()
        );

        WebhookEvent event = WebhookEvent.builder()
                .id(100L)
                .tenantId(10L)
                .build();

        service.queueExecution(rule, event);

        verify(publisher, never()).publish(any());
    }
}
