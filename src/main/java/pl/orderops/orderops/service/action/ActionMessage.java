package pl.orderops.orderops.service.action;

public class ActionMessage {

    private Long ruleRevisionId;
    private int actionIndex;
    private Long eventId;
    private String eventPayload;
    private int attempt;
    private Long retryAt;

    public ActionMessage() {
    }

    public ActionMessage(Long ruleRevisionId, int actionIndex, Long eventId, String eventPayload, int attempt, Long retryAt) {
        this.ruleRevisionId = ruleRevisionId;
        this.actionIndex = actionIndex;
        this.eventId = eventId;
        this.eventPayload = eventPayload;
        this.attempt = attempt;
        this.retryAt = retryAt;
    }

    public static ActionMessageBuilder builder() {
        return new ActionMessageBuilder();
    }

    public Long getRuleRevisionId() {
        return ruleRevisionId;
    }

    public void setRuleRevisionId(Long ruleRevisionId) {
        this.ruleRevisionId = ruleRevisionId;
    }

    public int getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(int actionIndex) {
        this.actionIndex = actionIndex;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(String eventPayload) {
        this.eventPayload = eventPayload;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public Long getRetryAt() {
        return retryAt;
    }

    public void setRetryAt(Long retryAt) {
        this.retryAt = retryAt;
    }

    public static class ActionMessageBuilder {
        private Long ruleRevisionId;
        private int actionIndex;
        private Long eventId;
        private String eventPayload;
        private int attempt;
        private Long retryAt;

        public ActionMessageBuilder ruleRevisionId(Long ruleRevisionId) {
            this.ruleRevisionId = ruleRevisionId;
            return this;
        }

        public ActionMessageBuilder actionIndex(int actionIndex) {
            this.actionIndex = actionIndex;
            return this;
        }

        public ActionMessageBuilder eventId(Long eventId) {
            this.eventId = eventId;
            return this;
        }

        public ActionMessageBuilder eventPayload(String eventPayload) {
            this.eventPayload = eventPayload;
            return this;
        }

        public ActionMessageBuilder attempt(int attempt) {
            this.attempt = attempt;
            return this;
        }

        public ActionMessageBuilder retryAt(Long retryAt) {
            this.retryAt = retryAt;
            return this;
        }

        public ActionMessage build() {
            return new ActionMessage(ruleRevisionId, actionIndex, eventId, eventPayload, attempt, retryAt);
        }
    }
}
