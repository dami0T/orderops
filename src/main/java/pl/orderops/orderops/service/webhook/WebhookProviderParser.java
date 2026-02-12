package pl.orderops.orderops.service.webhook;

import java.util.Map;
import pl.orderops.orderops.model.webhook.CanonicalWebhookEvent;

public interface WebhookProviderParser {

  boolean supports(String providerHeader);

  CanonicalWebhookEvent parse(String payload, Map<String, String> headers);
}