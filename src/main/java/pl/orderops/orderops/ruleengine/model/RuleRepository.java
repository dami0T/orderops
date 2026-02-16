package pl.orderops.orderops.ruleengine.model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.orderops.orderops.ruleengine.model.Rule;

public interface RuleRepository extends JpaRepository<Rule, Long> {

  List<Rule> findByTenantIdAndDeletedFalse(Long tenantId);

  @Query("""
            select r
            from RuleRevision r
            join r.rule rule
            where r.active = true
            and rule.active = true
            and rule.deleted = false
      """)
  List<Rule> findMatchingRules(Long tenantId, String eventType);
}
