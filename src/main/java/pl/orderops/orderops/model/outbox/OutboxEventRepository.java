package pl.orderops.orderops.model.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query("SELECT o FROM OutboxEvent o WHERE o.status IN (pl.orderops.orderops.model.outbox.OutboxEventStatus.PENDING, pl.orderops.orderops.model.outbox.OutboxEventStatus.RETRY) " +
           "AND (o.nextRetryAt IS NULL OR o.nextRetryAt <= :now) " +
           "ORDER BY o.createdAt ASC")
    List<OutboxEvent> findReadyToProcess(OffsetDateTime now);

    @Query("SELECT o FROM OutboxEvent o WHERE o.status = pl.orderops.orderops.model.outbox.OutboxEventStatus.PENDING AND o.createdAt < :cutoff ORDER BY o.createdAt ASC")
    List<OutboxEvent> findUnprocessedEventsOlderThan(OffsetDateTime cutoff);

    @Modifying
    @Query("UPDATE OutboxEvent o SET o.status = :status, o.processedAt = :processedAt WHERE o.id = :id")
    void markAsProcessed(Long id, OffsetDateTime processedAt, OutboxEventStatus status);

    @Modifying
    @Query("UPDATE OutboxEvent o SET o.status = pl.orderops.orderops.model.outbox.OutboxEventStatus.PROCESSING WHERE o.id = :id")
    void markAsProcessing(Long id);

    @Modifying
    @Query("UPDATE OutboxEvent o SET o.retryCount = :retryCount, o.nextRetryAt = :nextRetryAt, o.lastError = :lastError, o.status = :status WHERE o.id = :id")
    void markForRetry(Long id, Integer retryCount, OffsetDateTime nextRetryAt, String lastError, OutboxEventStatus status);
}
