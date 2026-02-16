package pl.orderops.orderops.service.action;

import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.ruleengine.model.RuntimeRule;

public interface ActionExecutorService {
  void queueExecution(Long revisionId, int actionIndex, Long eventId, String eventPayload);
  void queueExecution(RuntimeRule rule, WebhookEvent event);
}
