package pl.orderops.orderops.model.webhook;

public enum WebhookEventStatus {
    RECEIVED,
    MATCHED,
    ACTIONS_SCHEDULED,
    PROCESSING,
    PROCESSED,
    FAILED
}
