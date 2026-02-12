package pl.orderops.orderops.ruleengine.model;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import pl.orderops.orderops.action.model.ActionType;

@Data
@Builder
public class ActionDetailsResponse {
  private Long id;
  private ActionType type;
  private Map<String, Object> config;
}
