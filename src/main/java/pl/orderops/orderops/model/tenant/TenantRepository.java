package pl.orderops.orderops.model.tenant;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.orderops.orderops.model.tenant.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
  Optional<Tenant> findByApiKey(String apiKey);

  boolean existsByApiKey(String apiKey);
}
