package pl.orderops.orderops.ruleengine.model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRevisionRepository extends JpaRepository<RuleRevision, Long> {

  Optional<RuleRevision> findTopByRuleIdOrderByVersionDesc(Long ruleId);

  List<RuleRevision> findByActiveTrue();

  @Query("""
        select r
        from RuleRevision r
        join r.rule rule
        where r.active = true
        and rule.active = true
        and rule.deleted = false
    """)
  List<RuleRevision> findAllExecutable();
}
