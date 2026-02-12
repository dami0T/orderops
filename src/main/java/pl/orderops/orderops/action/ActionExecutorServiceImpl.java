package pl.orderops.orderops.action;

import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.action.model.ActionExecution;
import pl.orderops.orderops.action.model.ActionExecutionRepository;
import pl.orderops.orderops.action.model.ExecutionStatus;
import pl.orderops.orderops.model.webhook.WebhookEvent;

@Service
@RequiredArgsConstructor
public class ActionExecutorServiceImpl implements ActionExecutorService {

  private final ActionExecutionRepository executionRepository;

  @Override
  @Transactional
  public void execute(Action action, WebhookEvent event) {

    ActionExecution execution = ActionExecution.builder()
        .actionId(action.getId())
        .eventId(event.getId())
        .status(ExecutionStatus.PENDING)
        .attempts(0)
        .nextRetryAt(OffsetDateTime.now())
        .build();

    executionRepository.save(execution);
  }
}
