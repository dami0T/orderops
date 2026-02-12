package pl.orderops.orderops.ruleengine.lexer;

import java.util.*;
import java.util.regex.*;
import pl.orderops.orderops.ruleengine.lexer.Token;
import pl.orderops.orderops.ruleengine.lexer.TokenType;

public class Lexer {

  private static final Pattern TOKEN_PATTERN = Pattern.compile(
      "\\(|\\)|AND|OR|contains|==|!=|>=|<=|>|<|\"[^\"]*\"|[a-zA-Z0-9_.]+"
  );

  public static List<Token> tokenize(String input) {

    List<Token> tokens = new ArrayList<>();
    Matcher m = TOKEN_PATTERN.matcher(input);

    while (m.find()) {
      String t = m.group();

      switch (t) {
        case "(" -> tokens.add(new Token(TokenType.LPAREN, t));
        case ")" -> tokens.add(new Token(TokenType.RPAREN, t));
        case "AND" -> tokens.add(new Token(TokenType.AND, t));
        case "OR" -> tokens.add(new Token(TokenType.OR, t));
        case "==", "!=", ">", "<", ">=", "<=", "contains" ->
            tokens.add(new Token(TokenType.OPERATOR, t));
        default -> {
          if (t.startsWith("\""))
            tokens.add(new Token(TokenType.VALUE, t.replace("\"","")));
          else
            tokens.add(new Token(TokenType.PATH, t));
        }
      }
    }

    return tokens;
  }
}
