package pl.orderops.orderops.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionRequest {
  private Long ruleId;           // do której reguły przypisana
  private String type;           // HTTP, SLACK, MAIL, itp.
  private String configJson;     // JSON np. URL + template + headers
  private int maxAttempts = 3;   // ile prób retry
}
