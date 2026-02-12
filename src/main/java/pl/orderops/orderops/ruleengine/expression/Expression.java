package pl.orderops.orderops.ruleengine.expression;

public interface Expression {
  boolean evaluate(Object payload);
}
