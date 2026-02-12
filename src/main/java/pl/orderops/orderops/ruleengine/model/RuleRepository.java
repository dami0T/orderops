package pl.orderops.orderops.ruleengine.model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.orderops.orderops.ruleengine.model.Rule;

public interface RuleRepository extends JpaRepository<Rule, Long> {
  List<Rule> findByTenantIdAndDeletedFalse(Long tenantId);
  Optional<Rule> findByIdAndTenantId(Long id, Long tenantId);
  @Query("""
    select distinct r
    from Rule r
    left join fetch r.actions
    where r.tenant.id = :tenantId
      and r.deleted = false
""")
  List<Rule> findAllWithActions(Long tenantId);

  @Query("""
    select distinct r
    from Rule r
    left join fetch r.actions
    where r.tenant.id = :tenantId
      and r.eventName = :eventName
      and r.active = true
      and r.deleted = false
""")
  List<Rule> findMatchingRules(Long tenantId, String eventName);
}
