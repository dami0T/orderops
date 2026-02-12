package pl.orderops.orderops.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {
  private Long id;
  private String name;
  private String apiKey;
}
