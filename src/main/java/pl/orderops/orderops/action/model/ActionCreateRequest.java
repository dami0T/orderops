package pl.orderops.orderops.action.model;

import java.util.Map;
import lombok.Data;

@Data
public class ActionCreateRequest {

  private ActionType type;
  private Map<String, Object> config;
}
