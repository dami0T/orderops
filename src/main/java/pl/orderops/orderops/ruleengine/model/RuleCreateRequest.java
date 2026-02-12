package pl.orderops.orderops.ruleengine.model;

import java.util.List;
import java.util.Map;
import lombok.Data;
import pl.orderops.orderops.action.model.ActionCreateRequest;

@Data
public class RuleCreateRequest {

  private Long tenantId;
  private String eventName;
  private Map<String, Object> condition;
  private List<ActionCreateRequest> actions;
}
