package pl.orderops.orderops.ruleengine.model;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import pl.orderops.orderops.ruleengine.model.ActionDetailsResponse;

@Data
@Builder
public class RuleDetailsResponse {
  private Long id;
  private String eventName;
  private boolean active;
  private Map<String, Object> condition;
  private List<ActionDetailsResponse> actions;
}