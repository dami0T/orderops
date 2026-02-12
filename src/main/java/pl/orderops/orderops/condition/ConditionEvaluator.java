package pl.orderops.orderops.condition;

import com.jayway.jsonpath.JsonPath;

public class ConditionEvaluator {

  public static boolean evaluate(String conditionExpression, Object payload) {

    Condition c = ConditionParser.parse(conditionExpression);

    Object value;
    try {
      value = JsonPath.read(payload, "$." + c.path());
    } catch (Exception e) {
      return false;
    }

    if (value == null)
      return false;

    return compare(value, c.operator(), c.value());
  }

  private static boolean compare(Object left, String op, String rightRaw) {

    String leftStr = left.toString();

    switch (op) {

      case "==": return leftStr.equals(rightRaw);
      case "!=": return !leftStr.equals(rightRaw);
      case "contains": return leftStr.contains(rightRaw);

      case ">":
      case "<":
      case ">=":
      case "<=":
        double leftNum = Double.parseDouble(leftStr);
        double rightNum = Double.parseDouble(rightRaw);

        return switch (op) {
          case ">" -> leftNum > rightNum;
          case "<" -> leftNum < rightNum;
          case ">=" -> leftNum >= rightNum;
          case "<=" -> leftNum <= rightNum;
          default -> false;
        };
    }

    return false;
  }
}
