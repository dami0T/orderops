package pl.orderops.orderops.condition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.orderops.orderops.condition.Condition;

public class ConditionParser {

  private static final Pattern PATTERN =
      Pattern.compile("(.+?)\\s*(==|!=|>=|<=|>|<|contains)\\s*(.+)");

  public static Condition parse(String expression) {
    Matcher m = PATTERN.matcher(expression);

    if (!m.matches())
      throw new IllegalArgumentException("Invalid condition: " + expression);

    return new Condition(
        m.group(1).trim(),
        m.group(2).trim(),
        m.group(3).replace("\"", "").trim()
    );
  }
}
