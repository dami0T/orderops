package pl.orderops.orderops.condition;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.condition.Condition;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ConditionParser {

  private final ObjectMapper mapper;

  public ConditionNode parse(Map<String, Object> json) {
    return parseNode(json);
  }

  private ConditionNode parseNode(Map<String, Object> node) {

    if (node.containsKey("and"))
      return new LogicalNode(
          LogicalNode.Type.AND,
          parseChildren((List<Map<String, Object>>) node.get("and"))
      );

    if (node.containsKey("or"))
      return new LogicalNode(
          LogicalNode.Type.OR,
          parseChildren((List<Map<String, Object>>) node.get("or"))
      );

    if (node.containsKey("not"))
      return new LogicalNode(
          LogicalNode.Type.NOT,
          List.of(parseNode((Map<String, Object>) node.get("not")))
      );

    // comparison
    return new ComparisonNode(
        (String) node.get("path"),
        mapOperator((String) node.get("op")),
        node.get("value")
    );
  }

  private List<ConditionNode> parseChildren(List<Map<String, Object>> list) {
    return list.stream().map(this::parseNode).toList();
  }

  private ComparisonNode.Operator mapOperator(String op) {
    return switch (op) {
      case "=" -> ComparisonNode.Operator.EQ;
      case "!=" -> ComparisonNode.Operator.NEQ;
      case ">" -> ComparisonNode.Operator.GT;
      case "<" -> ComparisonNode.Operator.LT;
      case ">=" -> ComparisonNode.Operator.GTE;
      case "<=" -> ComparisonNode.Operator.LTE;
      case "contains" -> ComparisonNode.Operator.CONTAINS;
      default -> throw new IllegalArgumentException("Unknown operator " + op);
    };
  }
}
