package pl.orderops.orderops.condition;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogicalNode implements ConditionNode {

  public enum Type {
    AND, OR, NOT
  }

  private Type type;
  private List<ConditionNode> children;
}
