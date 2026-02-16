package pl.orderops.orderops.service.rule;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.condition.ConditionEvaluator;
import pl.orderops.orderops.ruleengine.cache.RuleRevisionCache;
import pl.orderops.orderops.ruleengine.model.Rule;
import pl.orderops.orderops.ruleengine.model.RuleRepository;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.ruleengine.model.RuntimeRule;
import pl.orderops.orderops.service.action.ActionExecutorService;
import pl.orderops.orderops.ruleengine.model.RuleRevision;
import pl.orderops.orderops.ruleengine.model.RuleRevisionRepository;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngine {

  private final RuleRevisionCache cache;
  private final ConditionEvaluator conditionEvaluator;
  private final ActionExecutorService actionExecutor;
  private final ObjectMapper objectMapper;

  public void process(WebhookEvent event) {

    List<RuntimeRule> rules =
        cache.get(event.getTenantId(), event.getEventType());

    for (RuntimeRule rule : rules) {

      if (rule.getConditionTree() != null &&
          !conditionEvaluator.evaluate(rule.getConditionTree(), event))
        continue;

      actionExecutor.queueExecution(rule, event);
    }
  }
}

