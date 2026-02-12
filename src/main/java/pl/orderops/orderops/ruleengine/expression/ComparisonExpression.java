package pl.orderops.orderops.ruleengine.expression;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ComparisonExpression implements Expression {

  private final String path;
  private final String operator;
  private final String value;

  @Override
  public boolean evaluate(Object payload) {

    Object extracted;
    try {
      extracted = JsonPath.read(payload, "$." + path);
    } catch (Exception e) {
      return false;
    }

    if (extracted == null)
      return false;

    return compare(extracted.toString(), operator, value);
  }

  private boolean compare(String left, String op, String right) {

    switch (op) {
      case "==": return left.equals(right);
      case "!=": return !left.equals(right);
      case "contains": return left.contains(right);

      case ">":
      case "<":
      case ">=":
      case "<=":
        double l = Double.parseDouble(left);
        double r = Double.parseDouble(right);

        return switch (op) {
          case ">" -> l > r;
          case "<" -> l < r;
          case ">=" -> l >= r;
          case "<=" -> l <= r;
          default -> false;
        };
    }
    return false;
  }
}
