package pl.orderops.orderops.ruleengine.model;

import lombok.Builder;
import lombok.Data;


public record RuleResponse (
   Long id,
   String eventType,
   boolean active,
   int actionsCount){
}
