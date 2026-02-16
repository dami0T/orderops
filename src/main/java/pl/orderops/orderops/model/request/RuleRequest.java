package pl.orderops.orderops.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleRequest {
  private Long tenantId;        // do którego tenant przypisana reguła
  private String eventType;     // np. ORDER_CREATED
  private boolean active = true;
  private String conditionJson; // JSON z warunkami AND/OR
}
