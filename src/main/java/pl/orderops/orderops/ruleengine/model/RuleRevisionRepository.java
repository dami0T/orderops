package pl.orderops.orderops.ruleengine.model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRevisionRepository extends JpaRepository<RuleRevision, Long> {

  Optional<RuleRevision> findTopByRuleIdOrderByVersionDesc(Long ruleId);

  @Query("""
      select rev
      from RuleRevision rev
      join fetch rev.rule r
      join fetch r.tenant t
      where rev.active = true
        and r.active = true
        and r.deleted = false
  """)
  List<RuleRevision> findAllExecutable();
}
