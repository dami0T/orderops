package pl.orderops.orderops.ruleengine.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RuleResponse {
  private Long id;
  private String eventName;
  private boolean active;
  private int actionsCount;
}
