package pl.orderops.orderops.service.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;
import pl.orderops.orderops.model.webhook.CanonicalWebhookEvent;
import pl.orderops.orderops.service.webhook.WebhookProviderParser;

@Component
public class AllegroWebhookParser implements WebhookProviderParser {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public boolean supports(String providerHeader) {
    return "allegro".equalsIgnoreCase(providerHeader);
  }

  @Override
  public CanonicalWebhookEvent parse(String payload, Map<String, String> headers) {
    try {
      JsonNode node = objectMapper.readTree(payload);

      return CanonicalWebhookEvent.builder()
          .provider("allegro")
          .externalEventId(node.get("id").asText())
          .eventName(node.get("type").asText())
          .payload(payload)
          .build();

    } catch (Exception e) {
      throw new RuntimeException("Invalid Allegro payload", e);
    }
  }
}
