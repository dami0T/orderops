package pl.orderops.orderops.ruleengine.model;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import pl.orderops.orderops.ruleengine.model.ActionDetailsResponse;


public record RuleDetailsResponse(
    Long id,
    String name,
    boolean active,
    Map<String, Object> trigger,
    Map<String, Object> conditions,
    List<Map<String, Object>> actions
) {}