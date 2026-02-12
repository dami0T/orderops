package pl.orderops.orderops.action;

import pl.orderops.orderops.model.webhook.WebhookEvent;

public interface ActionExecutorService {
  void execute(Action action, WebhookEvent event);
}
