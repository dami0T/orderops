package pl.orderops.orderops.service.rule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.orderops.orderops.condition.ConditionEvaluator;
import pl.orderops.orderops.condition.ConditionNode;
import pl.orderops.orderops.condition.ConditionParser;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.ruleengine.model.RuleRevision;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RuleMatcherTest {

    @Mock
    private ConditionParser conditionParser;

    @Mock
    private ConditionEvaluator conditionEvaluator;

    private RuleMatcher ruleMatcher;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        ruleMatcher = new RuleMatcher(conditionParser, conditionEvaluator, objectMapper);
    }

    @Test
    void matches_trigger_event_matches_returns_true() {
        RuleRevision revision = mock(RuleRevision.class);
        when(revision.getTriggerJson()).thenReturn("{\"event\": \"order.created\"}");
        when(revision.getConditionsJson()).thenReturn(null);

        WebhookEvent event = WebhookEvent.builder()
                .eventType("order.created")
                .source("allegro")
                .build();

        assertTrue(ruleMatcher.matches(revision, event));
    }

    @Test
    void matches_trigger_event_not_match_returns_false() {
        RuleRevision revision = mock(RuleRevision.class);
        when(revision.getTriggerJson()).thenReturn("{\"event\": \"order.created\"}");

        WebhookEvent event = WebhookEvent.builder()
                .eventType("order.paid")
                .source("allegro")
                .build();

        assertFalse(ruleMatcher.matches(revision, event));
    }

    @Test
    void matches_trigger_source_not_match_returns_false() {
        RuleRevision revision = mock(RuleRevision.class);
        when(revision.getTriggerJson()).thenReturn("{\"event\": \"order.created\", \"source\": \"stripe\"}");

        WebhookEvent event = WebhookEvent.builder()
                .eventType("order.created")
                .source("allegro")
                .build();

        assertFalse(ruleMatcher.matches(revision, event));
    }

    @Test
    void matches_condition_returns_true() {
        RuleRevision revision = mock(RuleRevision.class);
        when(revision.getTriggerJson()).thenReturn("{\"event\": \"order.created\"}");
        when(revision.getConditionsJson()).thenReturn("{}");

        when(conditionParser.parse(any())).thenReturn(mock(ConditionNode.class));
        lenient().when(conditionEvaluator.evaluate(any(), any())).thenReturn(true);

        WebhookEvent event = WebhookEvent.builder()
                .eventType("order.created")
                .source("allegro")
                .build();

        assertTrue(ruleMatcher.matches(revision, event));
    }

    @Test
    void matches_condition_returns_false() {
        RuleRevision revision = mock(RuleRevision.class);
        when(revision.getTriggerJson()).thenReturn("{\"event\": \"order.created\"}");
        when(revision.getConditionsJson()).thenReturn("{}");

        when(conditionParser.parse(any())).thenReturn(mock(ConditionNode.class));
        lenient().when(conditionEvaluator.evaluate(any(), any())).thenReturn(false);

        WebhookEvent event = WebhookEvent.builder()
                .eventType("order.created")
                .source("allegro")
                .build();

        assertFalse(ruleMatcher.matches(revision, event));
    }

    @Test
    void matches_no_conditions_returns_true() {
        RuleRevision revision = mock(RuleRevision.class);
        when(revision.getTriggerJson()).thenReturn("{\"event\": \"order.created\"}");
        when(revision.getConditionsJson()).thenReturn(null);

        WebhookEvent event = WebhookEvent.builder()
                .eventType("order.created")
                .source("allegro")
                .build();

        assertTrue(ruleMatcher.matches(revision, event));
        verify(conditionParser, never()).parse(any());
    }

    @Test
    void matches_empty_trigger_returns_true() {
        RuleRevision revision = mock(RuleRevision.class);
        when(revision.getTriggerJson()).thenReturn("{}");
        when(revision.getConditionsJson()).thenReturn(null);

        WebhookEvent event = WebhookEvent.builder()
                .eventType("anything")
                .source("any")
                .build();

        assertTrue(ruleMatcher.matches(revision, event));
    }
}
