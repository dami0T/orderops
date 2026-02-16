package pl.orderops.orderops.ruleengine.parser;

import java.util.List;
import pl.orderops.orderops.ruleengine.lexer.Token;
import pl.orderops.orderops.ruleengine.expression.ComparisonExpression;
import pl.orderops.orderops.ruleengine.expression.Expression;
import pl.orderops.orderops.ruleengine.expression.LogicalExpression;
import pl.orderops.orderops.ruleengine.lexer.TokenType;

public class Parser {

  private final List<Token> tokens;
  private int pos = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public Expression parse() {
    return parseOr();
  }

  private Expression parseOr() {
    Expression expr = parseAnd();

    while (match(TokenType.OR)) {
      Expression right = parseAnd();
      expr = new LogicalExpression(expr, right, LogicalExpression.Type.OR);
    }

    return expr;
  }

  private Expression parseAnd() {
    Expression expr = parsePrimary();

    while (match(TokenType.AND)) {
      Expression right = parsePrimary();
      expr = new LogicalExpression(expr, right, LogicalExpression.Type.AND);
    }

    return expr;
  }

  private Expression parsePrimary() {

    if (match(TokenType.LPAREN)) {
      Expression expr = parseOr();
      consume(TokenType.RPAREN);
      return expr;
    }

    return parseComparison();
  }

  private Expression parseComparison() {

    String path = consume(TokenType.PATH).value();
    String operator = consume(TokenType.OPERATOR).value();
    String value = consume(TokenType.VALUE, TokenType.PATH).value();

    return new ComparisonExpression(path, operator, value);
  }

  private boolean match(TokenType type) {
    if (check(type)) {
      pos++;
      return true;
    }
    return false;
  }

  private boolean check(TokenType type) {
    return pos < tokens.size() && tokens.get(pos).type() == type;
  }

  private Token consume(TokenType... types) {
    for (TokenType t : types)
      if (check(t)) return tokens.get(pos++);
    throw new RuntimeException("Unexpected token: " + tokens.get(pos));
  }
}
