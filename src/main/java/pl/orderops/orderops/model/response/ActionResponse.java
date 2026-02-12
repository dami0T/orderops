package pl.orderops.orderops.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionResponse {
  private Long id;
  private Long ruleId;
  private String type;
  private String configJson;
  private int maxAttempts;
  private LocalDateTime createdAt;
}
