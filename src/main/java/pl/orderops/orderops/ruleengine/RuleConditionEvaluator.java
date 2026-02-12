package pl.orderops.orderops.ruleengine;

import pl.orderops.orderops.ruleengine.lexer.Lexer;
import pl.orderops.orderops.ruleengine.parser.Parser;

public class RuleConditionEvaluator {

  public static boolean evaluate(String expression, Object payload) {

    var tokens = Lexer.tokenize(expression);
    var parser = new Parser(tokens);
    var tree = parser.parse();

    return tree.evaluate(payload);
  }
}
