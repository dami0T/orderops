package pl.orderops.orderops.service.tenant;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.model.response.TenantResponse;
import pl.orderops.orderops.model.tenant.Tenant;
import pl.orderops.orderops.model.tenant.TenantRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

  private final TenantRepository tenantRepository;

  public Tenant createTenant(String name) {
    String apiKey = generateApiKey();
    Tenant tenant = Tenant.builder()
        .name(name)
        .apiKey(apiKey)
        .createdAt(OffsetDateTime.now())
        .build();
    tenantRepository.save(tenant);
    return tenant;
  }

  public String generateApiKey() {
    byte[] randomBytes = new byte[32];
    new SecureRandom().nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  public List<TenantResponse> getAllTenants () {
    return tenantRepository.findAll()
        .stream()
        .map(tenant -> new TenantResponse(
            tenant.getId(),
            tenant.getName(),
            tenant.getApiKey()
        ))
        .toList();
  }

  public TenantResponse getTenant(Long id){
    return new TenantResponse();
  }
}
