package pl.orderops.orderops.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
import pl.orderops.orderops.service.rule.RuleCommandService;
import pl.orderops.orderops.ruleengine.model.Rule;
import pl.orderops.orderops.ruleengine.model.RuleDetailsResponse;
import pl.orderops.orderops.ruleengine.model.RuleRepository;
import pl.orderops.orderops.model.tenant.Tenant;
import pl.orderops.orderops.model.tenant.TenantRepository;
import pl.orderops.orderops.service.rule.RuleQueryService;
import pl.orderops.orderops.ruleengine.model.RuleToggleResponse;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
@Tag(name = "Rules", description = "Rule management endpoints")
public class RuleController {

  private final RuleRepository ruleRepository;
  private final RuleCommandService ruleCommandService;
  private final TenantRepository tenantRepository;
  private final RuleQueryService ruleService;

  @Operation(summary = "Get all rules", description = "Returns all rules in the system")
  @GetMapping
  public List<Rule> getAll() {
    return ruleRepository.findAll();
  }

  @Operation(summary = "Get tenant rules", description = "Returns all rules for a specific tenant")
  @GetMapping("/rules")
  public List<RuleDetailsResponse> getRules(
          @Parameter(description = "Tenant ID", required = true)
          @RequestParam Long tenantId) {
    return ruleService.getTenantRules(tenantId);
  }

  // TODO: Enable after implementation
  // @Operation(summary = "Create rule", description = "Creates a new rule")
  // @PostMapping
  // public Rule create(@RequestBody Map<String, Object> body) { ... }

  // @Operation(summary = "Update rule condition", description = "Updates rule condition JSON")
  // @PutMapping("/{id}/condition")
  // public Rule updateCondition(@PathVariable Long id, @RequestBody Map<String,String> body) { ... }

  // @Operation(summary = "Toggle rule", description = "Activates or deactivates a rule")
  // @PatchMapping("/rules/{id}/toggle")
  // public RuleToggleResponse toggleRule(@PathVariable Long id, @RequestParam Long tenantId) { ... }

  // @Operation(summary = "Delete rule", description = "Soft deletes a rule")
  // @DeleteMapping("/rules/{id}")
  // public ResponseEntity<Void> deleteRule(@PathVariable Long id, @RequestParam Long tenantId) { ... }
}
