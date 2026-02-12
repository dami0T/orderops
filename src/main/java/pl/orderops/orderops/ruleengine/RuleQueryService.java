package pl.orderops.orderops.ruleengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.orderops.orderops.ruleengine.model.Rule;
import pl.orderops.orderops.ruleengine.model.RuleDetailsResponse;
import pl.orderops.orderops.ruleengine.model.RuleRepository;
import pl.orderops.orderops.ruleengine.model.RuleRevision;
import pl.orderops.orderops.ruleengine.model.RuleRevisionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RuleQueryService {

  private final RuleRepository ruleRepository;
  private final RuleRevisionRepository revisionRepository;
  private final ObjectMapper objectMapper;

  public List<RuleDetailsResponse> getTenantRules(Long tenantId) {

    List<Rule> rules = ruleRepository.findByTenantIdAndDeletedFalse(tenantId);

    return rules.stream()
        .map(this::map)
        .toList();
  }

  private RuleDetailsResponse map(Rule rule) {

    RuleRevision rev = revisionRepository
        .findTopByRuleIdOrderByVersionDesc(rule.getId())
        .orElse(null);

    if (rev == null)
      return new RuleDetailsResponse(rule.getId(), rule.getName(), rule.isActive(), null, null, null);

    return new RuleDetailsResponse(
        rule.getId(),
        rule.getName(),
        rule.isActive(),
        read(rev.getTriggerJson()),
        read(rev.getConditionsJson()),
        read(rev.getActionsJson())
    );
  }

  private Map<String, Object> read(String json) {
    try {
      if (json == null) return null;
      return objectMapper.readValue(json, Map.class);
    } catch (Exception e) {
      return Map.of("invalid_json", true);
    }
  }
}
