package pl.orderops.orderops.action;

import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.action.model.ActionExecution;
import pl.orderops.orderops.action.model.ActionExecutionRepository;
import pl.orderops.orderops.action.model.ExecutionStatus;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.model.webhook.WebhookEventRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionExecutionWorker {

  private final ActionExecutionRepository executionRepository;
  private final ActionRepository actionRepository;
  private final WebhookEventRepository eventRepository;
  private final HttpActionExecutor httpExecutor;

  @Scheduled(fixedDelay = 5000)
  @Transactional
  public void poll() {

    List<ActionExecution> executions =
        executionRepository.findPendingOrRetry(OffsetDateTime.now());

    for (ActionExecution ex : executions) {
      process(ex);
    }
  }

  private void process(ActionExecution ex) {

    Action action = actionRepository.findById(ex.getActionId()).orElseThrow();
    WebhookEvent event = eventRepository.findById(ex.getEventId()).orElseThrow();

    try {
      httpExecutor.executeSync(action.getConfigJson(), event.getPayload());

      ex.setStatus(ExecutionStatus.SUCCESS);

    } catch (Exception e) {

      ex.setAttempts(ex.getAttempts() + 1);
      ex.setLastError(e.getMessage());

      if (ex.getAttempts() >= action.getMaxAttempts()) {
        ex.setStatus(ExecutionStatus.FAILED);
      } else {
        ex.setStatus(ExecutionStatus.RETRY);
        ex.setNextRetryAt(
            OffsetDateTime.now().plusSeconds((long) Math.pow(2, ex.getAttempts()) * 5)
        );
      }
    }
  }
}
