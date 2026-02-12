package pl.orderops.orderops.model.webhook;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.orderops.orderops.ruleengine.model.Rule;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
  Optional<WebhookEvent> findById(Long id);
  List<Rule> findByTenantIdAndEventName(String apiKey, String eventName);
}
