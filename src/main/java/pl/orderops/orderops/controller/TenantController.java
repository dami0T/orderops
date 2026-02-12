package pl.orderops.orderops.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.orderops.orderops.model.tenant.Tenant;
import pl.orderops.orderops.model.tenant.TenantRepository;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

  private final TenantRepository tenantRepository;

  @GetMapping
  public List<Tenant> getAll() {
    return tenantRepository.findAll();
  }

  @PostMapping
  public Tenant create(@RequestBody Map<String, String> body) {
    String name = body.get("name");
    if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name is required");

    // Tworzymy API key
    Tenant tenant = Tenant.builder()
        .name(name)
        .apiKey(UUID.randomUUID().toString())
        .build();
    return tenantRepository.save(tenant);
  }
}
