package pl.orderops.orderops.condition;

public record Condition(
    String path,
    String operator,
    String value
) {}
