package pl.orderops.orderops.ruleengine;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.ruleengine.model.Rule;
import pl.orderops.orderops.ruleengine.model.RuleRepository;
import pl.orderops.orderops.model.webhook.WebhookEvent;
import pl.orderops.orderops.action.ActionExecutorService;
import pl.orderops.orderops.ruleengine.model.RuleRevision;
import pl.orderops.orderops.ruleengine.model.RuleRevisionRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngine {

  private final RuleRevisionRepository revisionRepository;
  private final RuleMatcher matcher;
  private final ActionExecutor actionExecutor;

  @Override
  @Transactional
  public void process(WebhookEvent event) {

    List<RuleRevision> revisions =
        revisionRepository.findAllActiveForTenant(event.getTenantId());

    for (RuleRevision revision : revisions) {

      if (matcher.matches(revision.getTrigger(), event)) {

        executeRevision(revision, event);
      }
    }
  }

  private void executeRevision(RuleRevision revision, WebhookEvent event) {

    for (RuleAction action : revision.getActions()) {
      actionExecutor.execute(action, event);
    }
  }
}

