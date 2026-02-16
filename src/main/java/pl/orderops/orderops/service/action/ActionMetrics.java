package pl.orderops.orderops.service.action;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ActionMetrics {

    private final Counter actionsPublished;
    private final Counter actionsSucceeded;
    private final Counter actionsFailed;
    private final Counter actionsRetried;

    public ActionMetrics(MeterRegistry registry) {
        this.actionsPublished = Counter.builder("orderops.actions.published")
                .description("Total number of actions published to queue")
                .register(registry);

        this.actionsSucceeded = Counter.builder("orderops.actions.succeeded")
                .description("Total number of actions executed successfully")
                .register(registry);

        this.actionsFailed = Counter.builder("orderops.actions.failed")
                .description("Total number of actions that failed")
                .register(registry);

        this.actionsRetried = Counter.builder("orderops.actions.retried")
                .description("Total number of action retries")
                .register(registry);
    }

    public void incrementPublished() {
        actionsPublished.increment();
    }

    public void incrementSucceeded() {
        actionsSucceeded.increment();
    }

    public void incrementFailed() {
        actionsFailed.increment();
    }

    public void incrementRetried() {
        actionsRetried.increment();
    }
}
