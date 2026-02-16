package pl.orderops.orderops.service.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.orderops.orderops.condition.ConditionEvaluator;
import pl.orderops.orderops.condition.ConditionNode;
import pl.orderops.orderops.condition.ConditionParser;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.ruleengine.model.RuleRevision;

@RequiredArgsConstructor
@Component
public class RuleMatcher {


  private final ConditionParser conditionParser;
  private final ConditionEvaluator conditionEvaluator;
  private final ObjectMapper objectMapper;

  public boolean matches(RuleRevision revision, WebhookEvent event) {

    Map<String, Object> trigger =
        readJson(revision.getTriggerJson());

    if (!triggerMatches(trigger, event)) {
      return false;
    }

    Map<String, Object> conditions =
        readJson(revision.getConditionsJson());

    return conditionsMatch(conditions, event);
  }

  private boolean conditionsMatch(
      Map<String, Object> conditions,
      WebhookEvent event
  ) {

    if (conditions == null || conditions.isEmpty()) {
      return true; // brak warunków = zawsze true
    }

    ConditionNode ast =
        conditionParser.parse(conditions);

    return conditionEvaluator.evaluate(ast, event);
  }

  private Map<String, Object> readJson(String json) {
    try {
      return objectMapper.readValue(json, Map.class);
    } catch (Exception e) {
      return Map.of();
    }
  }


  private boolean triggerMatches(Map<String, Object> trigger, WebhookEvent event) {

    String expectedEvent = (String) trigger.get("event");
    if (expectedEvent != null && !expectedEvent.equals(event.getEventType())) {
      return false;
    }

    String source = (String) trigger.get("source");
    if (source != null && !source.equals(event.getSource())) {
      return false;
    }

    return true;
  }
}