package pl.orderops.orderops.ruleengine.expression;

import lombok.RequiredArgsConstructor;
import pl.orderops.orderops.ruleengine.expression.Expression;

@RequiredArgsConstructor
public class LogicalExpression implements Expression {

  public enum Type { AND, OR }

  private final Expression left;
  private final Expression right;
  private final Type type;

  @Override
  public boolean evaluate(Object payload) {
    return switch (type) {
      case AND -> left.evaluate(payload) && right.evaluate(payload);
      case OR  -> left.evaluate(payload) || right.evaluate(payload);
    };
  }
}