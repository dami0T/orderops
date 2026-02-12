package pl.orderops.orderops.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.model.event.ProcessedEvent;
import pl.orderops.orderops.model.event.ProcessedEventRepository;
import pl.orderops.orderops.model.tenant.Tenant;
import pl.orderops.orderops.model.tenant.TenantRepository;
import pl.orderops.orderops.model.webhook.CanonicalWebhookEvent;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.model.webhook.WebhookEventRepository;
import pl.orderops.orderops.ruleengine.RuleEngine;
import pl.orderops.orderops.service.webhook.WebhookProviderResolver;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

  private final WebhookEventRepository webhookEventRepository;
  private final TenantRepository tenantRepository;
  private final RuleEngine ruleEngine;
  private final ProcessedEventRepository processedEventRepository;
  private final ObjectMapper objectMapper;
  private final WebhookProviderResolver webhookProviderResolver;

  @Transactional
  public void handleIncoming(String apiKey, String provider, String payload, Map<String,String> headers) {

    Tenant tenant = tenantRepository.findByApiKey(apiKey)
        .orElseThrow(() -> new RuntimeException("Invalid API Key"));

    CanonicalWebhookEvent event = webhookProviderResolver.resolve(provider, payload, headers);

    // IDEMPOTENCJA
    try {
      processedEventRepository.save(
          new ProcessedEvent(null, tenant.getId(), event.getExternalEventId(), OffsetDateTime.now())
      );
    } catch (DataIntegrityViolationException ex) {
      log.info("Duplicate event ignored {}", event.getExternalEventId());
      return;
    }

    WebhookEvent entity = WebhookEvent.builder()
        .tenantId(tenant.getId())
        .eventName(event.getEventName())
        .payload(event.getPayload())
        .createdAt(OffsetDateTime.now())
        .build();

    webhookEventRepository.save(entity);
    ruleEngine.processAsync(entity);
  }

  private String extractEventName(String payload) {
    try {
      return objectMapper.readTree(payload).get("event").asText();
    } catch (Exception e) {
      log.warn("Failed to extract event name, defaulting to 'unknown'", e);
      return "unknown";
    }
  }

  private String extractExternalId(String payload) {
    try {
      JsonNode node = objectMapper.readTree(payload);
      return node.path("id").asText(null);
    } catch (Exception e) {
      throw new RuntimeException("Cannot extract external event id", e);
    }
  }
}
