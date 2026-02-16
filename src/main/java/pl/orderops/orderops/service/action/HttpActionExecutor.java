package pl.orderops.orderops.service.action;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import pl.orderops.orderops.model.WebhookHttpException;
import pl.orderops.orderops.service.TemplateRenderer;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
@Component
@Slf4j
public class HttpActionExecutor {

  private final WebClient webClient;
  private final ObjectMapper mapper;
  private final TemplateRenderer templateRenderer;

  public void executeSync(String configJson, String payload) throws Exception {

    JsonNode cfg = mapper.readTree(configJson);

    String url = cfg.get("url").asText();
    String template = cfg.path("template").asText("");

    String message = templateRenderer.render(template, payload);

    Map<String,String> body = Map.of("text", message);

    webClient.post()
        .uri(url)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    log.info("Slack message sent: {}", message);
  }
}