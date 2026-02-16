package pl.orderops.orderops.ruleengine.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.orderops.orderops.condition.ConditionNode;

@Getter
@AllArgsConstructor
public class RuntimeRule {

  private Long revisionId;
  private Long tenantId;
  private String eventType;

  private ConditionNode conditionTree;
  private List<Map<String, Object>> actions;
}
