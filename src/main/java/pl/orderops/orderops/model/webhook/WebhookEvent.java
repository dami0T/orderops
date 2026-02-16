package pl.orderops.orderops.model.webhook;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "webhook_event")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // tenant
  @Column(nullable = false)
  private Long tenantId;

  // typ zdarzenia (invoice.created)
  @Column(nullable = false)
  private String eventType;

  // system źródłowy
  @Column(nullable = false)
  private String source;

  // id obiektu po stronie klienta
  private String externalId;

  // klucz idempotencyjny
  @Column(unique = true)
  private String idempotencyKey;

  // payload RAW JSON — świętość
  @Lob
  @Column(nullable = false, columnDefinition = "TEXT")
  private String payloadJson;

  // do szybkiego filtrowania (opcjonalne ale mega ważne)
  private BigDecimal amount;
  private String currency;
  private String country;

  // status przetwarzania
  @Enumerated(EnumType.STRING)
  private WebhookEventStatus status;

  private OffsetDateTime receivedAt;
  private OffsetDateTime processedAt;
}
