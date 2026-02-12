package pl.orderops.orderops.ruleengine.model;

import lombok.Data;

@Data
public class CreateRuleRequest {

  private String name;
  private String triggerJson;
  private String conditionsJson;
  private String actionsJson;
}
