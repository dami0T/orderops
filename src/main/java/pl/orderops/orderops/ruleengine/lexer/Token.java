package pl.orderops.orderops.ruleengine.lexer;
public record Token(TokenType type, String value) {}

enum TokenType {
  LPAREN, RPAREN,
  AND, OR,
  PATH, OPERATOR, VALUE
}
