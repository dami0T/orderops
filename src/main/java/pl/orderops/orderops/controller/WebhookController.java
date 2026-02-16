package pl.orderops.orderops.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.service.rule.RuleEngine;
import pl.orderops.orderops.service.webhook.WebhookService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhooks", description = "Webhook ingestion endpoints")
@SecurityRequirement(name = "API Key")
public class WebhookController {

  private final WebhookService webhookService;
  private final RuleEngine ruleEngine;

  @Operation(
          summary = "Receive webhook",
          description = "Receives webhook events from external providers (Allegro, Stripe, etc.)"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Webhook accepted and queued"),
          @ApiResponse(responseCode = "400", description = "Bad request - invalid payload", content = @Content),
          @ApiResponse(responseCode = "401", description = "Unauthorized - invalid API key", content = @Content),
          @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping("/webhook")
  public ResponseEntity<String> receive(
          @Parameter(description = "API key for tenant authentication", required = true)
          @RequestHeader("X-API-Key") String apiKey,
          @Parameter(description = "Webhook provider (allegro, stripe, etc.)", required = true)
          @RequestHeader("X-Provider") String provider,
          @Parameter(description = "Raw webhook payload", required = true)
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

  @Operation(
          summary = "Process webhook event",
          description = "Internal endpoint for processing validated webhook events"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Event processed successfully")
  })
  @PostMapping("/events")
  public void receive(@RequestBody WebhookEvent event) {
    ruleEngine.process(event);
  }

}
