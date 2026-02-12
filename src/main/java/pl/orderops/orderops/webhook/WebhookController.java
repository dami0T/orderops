package pl.orderops.orderops.webhook;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.orderops.orderops.model.tenant.TenantRepository;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.ruleengine.RuleEngine;
import pl.orderops.orderops.webhook.WebhookService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

  private final WebhookService webhookService;
  private final RuleEngine ruleEngine;

  @PostMapping("/webhook")
  public ResponseEntity<String> receive(
      @RequestHeader("X-API-Key") String apiKey,
      @RequestHeader("X-Provider") String provider,
      @RequestBody String payload,
      @RequestHeader Map<String, String> headers
  ) {
    try {
      log.info("WEBHOOK IN provider={} apiKey={}", provider, apiKey);

      webhookService.handleIncoming(apiKey, provider, payload, headers);

      return ResponseEntity.ok("accepted");
    }
    catch (IllegalArgumentException e) {
      log.warn("BAD REQUEST: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
    catch (SecurityException e) {
      log.warn("UNAUTHORIZED: {}", e.getMessage());
      return ResponseEntity.status(401).body(e.getMessage());
    }
    catch (Exception e) {
      log.error("WEBHOOK ERROR", e);
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }
  @PostMapping("/events")
  public void receive(@RequestBody WebhookEvent event) {
    ruleEngine.processAsync(event);
  }

}
