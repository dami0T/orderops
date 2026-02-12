package pl.orderops.orderops.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.orderops.orderops.ruleengine.RuleCommandService;
import pl.orderops.orderops.ruleengine.model.Rule;
import pl.orderops.orderops.ruleengine.model.RuleDetailsResponse;
import pl.orderops.orderops.ruleengine.model.RuleRepository;
import pl.orderops.orderops.model.tenant.Tenant;
import pl.orderops.orderops.model.tenant.TenantRepository;
import pl.orderops.orderops.ruleengine.RuleQueryService;
import pl.orderops.orderops.ruleengine.model.RuleToggleResponse;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {

  private final RuleRepository ruleRepository;
  private final RuleCommandService ruleCommandService;
  private final TenantRepository tenantRepository;
  private final RuleQueryService ruleService;

  @GetMapping
  public List<Rule> getAll() {
    return ruleRepository.findAll();
  }

  @PostMapping
  public Rule create(@RequestBody Map<String, Object> body) {
    Long tenantId = Long.valueOf(body.get("tenantId").toString());
    String eventName = (String) body.get("eventName");

    Tenant tenant = tenantRepository.findById(tenantId)
        .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

    Rule rule = Rule.builder()
        .tenant(tenant)
        .eventName(eventName)
        .active(true)
        .build();
    return ruleRepository.save(rule);
  }

  @PutMapping("/{id}/condition")
  public Rule updateCondition(@PathVariable Long id, @RequestBody Map<String,String> body) {
    Rule rule = ruleRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Rule not found"));
    rule.setConditionJson(body.get("condition"));
    return ruleRepository.save(rule);
  }

  @GetMapping("/rules")
  public List<RuleDetailsResponse> getRules(@RequestParam Long tenantId) {
    return ruleService.getTenantRules(tenantId);
  }

  @PatchMapping("/rules/{id}/toggle")
  public RuleToggleResponse toggleRule(
      @PathVariable Long id,
      @RequestParam Long tenantId
  ) {
    return ruleCommandService.toggle(tenantId, id);
  }

  @DeleteMapping("/rules/{id}")
  public ResponseEntity<Void> deleteRule(
      @PathVariable Long id,
      @RequestParam Long tenantId
  ) {
    ruleCommandService.delete(tenantId, id);
    return ResponseEntity.noContent().build();
  }
}
