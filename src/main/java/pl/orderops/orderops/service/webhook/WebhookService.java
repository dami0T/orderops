package pl.orderops.orderops.service.webhook;

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
import pl.orderops.orderops.model.outbox.OutboxEvent;
import pl.orderops.orderops.model.outbox.OutboxEventRepository;
import pl.orderops.orderops.model.outbox.OutboxEventStatus;
import pl.orderops.orderops.model.tenant.Tenant;
import pl.orderops.orderops.model.tenant.TenantRepository;
import pl.orderops.orderops.model.webhook.CanonicalWebhookEvent;
import pl.orderops.orderops.model.webhook.WebhookEventStatus;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.model.webhook.WebhookEventRepository;
import pl.orderops.orderops.service.webhook.WebhookProviderResolver;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

  private final WebhookEventRepository webhookEventRepository;
  private final TenantRepository tenantRepository;
  private final ProcessedEventRepository processedEventRepository;
  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;
  private final WebhookProviderResolver webhookProviderResolver;

  @Transactional
  public void handleIncoming(String apiKey, String provider, String payload, Map<String,String> headers) {

    // 1️⃣ znajdź tenant po apiKey
    Tenant tenant = tenantRepository.findByApiKey(apiKey)
        .orElseThrow(() -> new SecurityException("Invalid API Key"));

    // 2️⃣ parsuj payload na ujednolicony event
    CanonicalWebhookEvent canonicalEvent = webhookProviderResolver.resolve(provider, payload, headers);

    // 3️⃣ IDEMPOTENCJA: jeśli event już został przetworzony, ignoruj
    try {
      processedEventRepository.save(
          new ProcessedEvent(null, tenant.getId(), canonicalEvent.getExternalEventId(), OffsetDateTime.now())
      );
    } catch (DataIntegrityViolationException ex) {
      log.info("Duplicate event ignored {}", canonicalEvent.getExternalEventId());
      return;
    }

    // 4️⃣ Zapisz WebhookEvent do bazy (source of truth)
    WebhookEvent eventEntity = WebhookEvent.builder()
        .tenantId(tenant.getId())
        .eventType(canonicalEvent.getEventType())
        .source(provider)
        .externalId(canonicalEvent.getExternalEventId())
        .payloadJson(canonicalEvent.getPayload())
        .status(WebhookEventStatus.RECEIVED)
        .receivedAt(OffsetDateTime.now())
        .build();

    webhookEventRepository.save(eventEntity);

    log.info("Saved webhook event id={} type={} tenant={}",
        eventEntity.getId(), eventEntity.getEventType(), tenant.getId());

    // 5️⃣ Zapisz do outbox (w tej samej transakcji)
    String eventPayload = String.format("{\"eventId\":%d,\"tenantId\":%d,\"eventType\":\"%s\"}",
        eventEntity.getId(), tenant.getId(), eventEntity.getEventType());
    
    OutboxEvent outboxEvent = OutboxEvent.builder()
        .aggregateType("WebhookEvent")
        .aggregateId(eventEntity.getId())
        .eventType("WEBHOOK_RECEIVED")
        .payloadJson(eventPayload)
        .createdAt(OffsetDateTime.now())
        .status(OutboxEventStatus.PENDING)
        .build();
    
    outboxEventRepository.save(outboxEvent);

    // 6️⃣ RuleEngine zostanie wywołany przez OutboxScheduler po zacommitowaniu transakcji
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
