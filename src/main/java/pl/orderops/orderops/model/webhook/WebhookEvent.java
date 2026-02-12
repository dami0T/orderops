package pl.orderops.orderops.model.webhook;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "webhook_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long tenantId;
  private String provider;
  private String eventName;

  @Column(columnDefinition = "TEXT")
  private String payload;

  private OffsetDateTime createdAt;
}
