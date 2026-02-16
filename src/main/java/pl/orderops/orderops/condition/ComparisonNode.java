package pl.orderops.orderops.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComparisonNode implements ConditionNode {

  public enum Operator {
    EQ, NEQ, GT, LT, GTE, LTE, CONTAINS
  }

  private String path;
  private Operator operator;
  private Object value;
}
