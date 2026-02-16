package pl.orderops.orderops.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.orderops.orderops.ruleengine.model.RuleRepository;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionController {

 // private final ActionRepository actionRepository;
  private final RuleRepository ruleRepository;

//  @GetMapping
//  public List<Action> getAll() {
//    return actionRepository.findAll();
//  }
//
//  @PostMapping
//  public Action create(@RequestBody Map<String, Object> body) throws JsonProcessingException {
//    Long ruleId = Long.valueOf(body.get("ruleId").toString());
//    String type = (String) body.get("type");
//    String config = new ObjectMapper().writeValueAsString(body.get("config"));
//
//    Rule rule = ruleRepository.findById(ruleId)
//        .orElseThrow(() -> new IllegalArgumentException("Rule not found"));
//
//    Action action = Action.builder()
//        .rule(rule)
//        .type(ActionType.valueOf(type))
//        .configJson(config)
//        .maxAttempts(3)
//        .build();
//    return actionRepository.save(action);
//  }
}
