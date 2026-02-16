package pl.orderops.orderops.service.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.model.tenant.Tenant;
import pl.orderops.orderops.model.tenant.TenantRepository;
import pl.orderops.orderops.ruleengine.cache.RuleCacheRefresher;
import pl.orderops.orderops.ruleengine.model.Rule;
import pl.orderops.orderops.ruleengine.model.RuleCreateRequest;
import pl.orderops.orderops.ruleengine.model.RuleRepository;
import pl.orderops.orderops.ruleengine.model.RuleResponse;
import pl.orderops.orderops.ruleengine.model.RuleRevision;
import pl.orderops.orderops.ruleengine.model.RuleRevisionRepository;
import pl.orderops.orderops.ruleengine.model.RuleToggleResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class RuleCommandService {

  private final RuleRepository ruleRepository;
  private final RuleCacheRefresher cacheRefresher;
  private final RuleRevisionRepository revisionRepository;
  private final TenantRepository tenantRepository;
  private final ObjectMapper objectMapper;

  public RuleResponse create(RuleCreateRequest request) {

    Tenant tenant = tenantRepository.findById(request.getTenantId())
        .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

    // 1️⃣ tworzymy kontener
    Rule rule = Rule.builder()
        .tenant(tenant)
        .name(request.getEventType())
        .active(true)
        .build();

    ruleRepository.save(rule);

    // 2️⃣ zapisujemy snapshot workflow
    RuleRevision revision = RuleRevision.builder()
        .rule(rule)
        .version(1)
        .active(true)
        .triggerJson(write(request.getTrigger()))
        .conditionsJson(write(request.getCondition()))
        .actionsJson(write(request.getActions()))
        .createdAt(java.time.OffsetDateTime.now())
        .build();

    revisionRepository.save(revision);
    cacheRefresher.refresh();
    return new RuleResponse(rule.getId(), rule.getName(), true, 1);
  }

  private String write(Object o) {
    try {
      return objectMapper.writeValueAsString(o);
    } catch (Exception e) {
      throw new RuntimeException("Invalid JSON", e);
    }
  }
}
