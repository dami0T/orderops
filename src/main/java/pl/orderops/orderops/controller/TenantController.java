package pl.orderops.orderops.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tenants", description = "Tenant management endpoints")
public class TenantController {

  private final TenantRepository tenantRepository;

  @Operation(summary = "Get all tenants", description = "Returns all tenants in the system")
  @GetMapping
  public List<Tenant> getAll() {
    return tenantRepository.findAll();
  }

  @Operation(
          summary = "Create tenant",
          description = "Creates a new tenant with auto-generated API key",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Tenant name",
                  required = true,
                  content = @io.swagger.v3.oas.annotations.media.Content(
                          mediaType = "application/json",
                          schema = @io.swagger.v3.oas.annotations.media.Schema(example = "{\"name\": \"My Store\"}")
                  )
          )
  )
  @PostMapping
  public Tenant create(@RequestBody Map<String, String> body) {
    String name = body.get("name");
    if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name is required");

    Tenant tenant = Tenant.builder()
        .name(name)
        .apiKey(UUID.randomUUID().toString())
        .build();
    return tenantRepository.save(tenant);
  }
}
