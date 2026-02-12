package pl.orderops.orderops.ruleengine;

import org.springframework.stereotype.Component;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.ruleengine.model.RuleTrigger;

@Component
public class RuleMatcher {

  public boolean matches(RuleTrigger trigger, WebhookEvent event) {

    if (!trigger.getEventType().equals(event.getType()))
      return false;

    if (trigger.getStatus() != null &&
        !trigger.getStatus().equals(event.getStatus()))
      return false;

    return true;
  }
}