package pl.orderops.orderops.action.model;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.orderops.orderops.action.model.ActionExecution;

public interface ActionExecutionRepository extends JpaRepository<ActionExecution, Long> {


  // Pobiera akcje PENDING lub RETRY do wykonania
  @Query("""
SELECT e FROM ActionExecution e
WHERE e.status IN ('PENDING','RETRY')
AND e.nextRetryAt <= :now
ORDER BY e.createdAt
""")
  List<ActionExecution> findPendingOrRetry(OffsetDateTime now);

  List<ActionExecution> findByEventId(Long eventId);
}
