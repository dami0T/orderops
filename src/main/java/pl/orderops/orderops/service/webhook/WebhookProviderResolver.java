package pl.orderops.orderops.service.webhook;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.orderops.orderops.model.webhook.CanonicalWebhookEvent;
import pl.orderops.orderops.service.webhook.WebhookProviderParser;

@Component
@RequiredArgsConstructor
public class WebhookProviderResolver {

  private final List<WebhookProviderParser> parsers;

  public CanonicalWebhookEvent resolve(String provider, String payload, Map<String, String> headers) {

    return parsers.stream()
        .filter(p -> p.supports(provider))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Unsupported provider: " + provider))
        .parse(payload, headers);
  }
}
