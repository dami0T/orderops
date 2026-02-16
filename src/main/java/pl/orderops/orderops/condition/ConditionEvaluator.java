package pl.orderops.orderops.condition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ConditionEvaluator {
  private final ObjectMapper mapper;

  public boolean evaluate(ConditionNode node, WebhookEvent event) {

    if (node instanceof LogicalNode logical)
      return evalLogical(logical, event);

    if (node instanceof ComparisonNode cmp)
      return evalComparison(cmp, event);

    return false;
  }

  private boolean evalLogical(LogicalNode node, WebhookEvent event) {

    return switch (node.getType()) {

      case AND -> node.getChildren()
          .stream()
          .allMatch(n -> evaluate(n, event));

      case OR -> node.getChildren()
          .stream()
          .anyMatch(n -> evaluate(n, event));

      case NOT -> !evaluate(node.getChildren().get(0), event);
    };
  }

  private boolean evalComparison(ComparisonNode node, WebhookEvent event) {

    Object actual = readPath(event.getPayloadJson(), node.getPath());
    Object expected = node.getValue();

    if (actual == null)
      return false;

    return switch (node.getOperator()) {

      case EQ -> actual.equals(expected);
      case NEQ -> !actual.equals(expected);

      case GT -> toNumber(actual) > toNumber(expected);
      case LT -> toNumber(actual) < toNumber(expected);
      case GTE -> toNumber(actual) >= toNumber(expected);
      case LTE -> toNumber(actual) <= toNumber(expected);

      case CONTAINS -> actual.toString().contains(expected.toString());
    };
  }

  private Object readPath(String payloadJson, String path) {

    try {
      JsonNode node = mapper.readTree(payloadJson);

      for (String part : path.split("\\.")) {
        node = node.get(part);
        if (node == null)
          return null;
      }

      if (node.isNumber()) return node.numberValue();
      if (node.isTextual()) return node.textValue();
      if (node.isBoolean()) return node.booleanValue();

      return node.toString();

    } catch (Exception e) {
      return null;
    }
  }

  private double toNumber(Object o) {
    return Double.parseDouble(o.toString());
  }
}
