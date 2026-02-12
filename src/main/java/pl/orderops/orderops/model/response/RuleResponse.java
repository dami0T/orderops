package pl.orderops.orderops.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleResponse {
  private Long id;
  private Long tenantId;
  private String eventName;
  private boolean active;
  private String conditionJson;
  private LocalDateTime createdAt;
}