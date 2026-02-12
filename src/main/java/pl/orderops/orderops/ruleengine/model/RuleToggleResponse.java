package pl.orderops.orderops.ruleengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RuleToggleResponse {
  private Long id;
  private boolean active;
}
