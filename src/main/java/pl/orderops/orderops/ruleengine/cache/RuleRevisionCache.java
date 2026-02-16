package pl.orderops.orderops.ruleengine.cache;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.orderops.orderops.condition.ConditionNode;
import pl.orderops.orderops.condition.ConditionParser;
import pl.orderops.orderops.ruleengine.model.RuleRevision;
import pl.orderops.orderops.ruleengine.model.RuleRevisionRepository;
import pl.orderops.orderops.ruleengine.model.RuntimeRule;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RuleRevisionCache {

  private final RuleRevisionRepository revisionRepository;
  private final ConditionParser parser;
  private final ObjectMapper mapper;

  private final Map<Long, Map<String, List<RuntimeRule>>> cache = new ConcurrentHashMap<>();

  @PostConstruct
  public void load() {
    reloadAll();
  }

  public void reloadAll() {

    cache.clear();

    List<RuleRevision> revisions = revisionRepository.findAllExecutable();

    for (RuleRevision rev : revisions) {

      RuntimeRule rule = compile(rev);

      Long tenantId = rev.getRule().getTenant().getId();
      String event = rev.getRule().getEventType();

      cache
          .computeIfAbsent(tenantId, t -> new ConcurrentHashMap<>())
          .computeIfAbsent(event, e -> new ArrayList<>())
          .add(rule);
    }
  }

  private RuntimeRule compile(RuleRevision rev) {

    ConditionNode ast = null;

    if (rev.getConditionsJson() != null) {
      try {
        Map<String,Object> map =
            mapper.readValue(rev.getConditionsJson(), Map.class);

        ast = parser.parse(map);

      } catch (Exception e) {
        throw new RuntimeException("Invalid rule json id=" + rev.getId(), e);
      }
    }

    List<Map<String,Object>> actions;

    try {
      actions = mapper.readValue(rev.getActionsJson(), List.class);
    } catch (Exception e) {
      actions = List.of();
    }

    return new RuntimeRule(
        rev.getId(),
        rev.getRule().getTenant().getId(),
        rev.getRule().getEventType(), // ← TU
        ast,
        actions
    );
  }

  public List<RuntimeRule> get(Long tenantId, String eventType) {

    return cache
        .getOrDefault(tenantId, Map.of())
        .getOrDefault(eventType, List.of());
  }
}
