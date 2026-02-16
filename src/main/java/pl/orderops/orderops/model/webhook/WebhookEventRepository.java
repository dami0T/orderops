package pl.orderops.orderops.model.webhook;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.orderops.orderops.ruleengine.model.Rule;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
  Optional<WebhookEvent> findById(Long id);
  List<WebhookEvent> findByTenantIdAndEventType(Long tenantId, String eventType);

  @Modifying
  @Query("UPDATE WebhookEvent w SET w.status = :status, w.processedAt = :processedAt WHERE w.id = :id")
  void updateStatus(Long id, WebhookEventStatus status, OffsetDateTime processedAt);

  @Modifying
  @Query("UPDATE WebhookEvent w SET w.status = :status WHERE w.id = :id")
  void updateStatus(Long id, WebhookEventStatus status);
}
