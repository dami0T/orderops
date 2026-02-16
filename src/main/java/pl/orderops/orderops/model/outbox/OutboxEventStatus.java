package pl.orderops.orderops.model.outbox;

public enum OutboxEventStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    RETRY,
    FAILED
}
