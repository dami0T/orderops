package pl.orderops.orderops.action.model;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.orderops.orderops.action.model.ActionExecution;

public interface ActionExecutionRepository extends JpaRepository<ActionExecution, Long> {


  // Pobiera akcje PENDING lub RETRY do wykonania
  @Query(value = """
        SELECT * FROM action_execution
        WHERE (status = 'PENDING' OR status = 'RETRY')
          AND next_retry_at <= :now
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
  List<ActionExecution> findPendingOrRetry(@Param("now") OffsetDateTime now);
}
