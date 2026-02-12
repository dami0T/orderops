package pl.orderops.orderops.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequest {
  private String name;          // nazwa klienta/tenant
  private String apiKey;        // opcjonalnie, można generować w backendzie
}
