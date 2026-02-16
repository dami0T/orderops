package pl.orderops.orderops.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import pl.orderops.orderops.ruleengine.model.RuleRepository;
import pl.orderops.orderops.model.tenant.TenantRepository;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

  private final TenantRepository tenantRepository;
  private final RuleRepository ruleRepository;
 // private final ActionRepository actionRepository;

//  @Bean
//  CommandLineRunner initData() {
//    return args -> {
//
//      // nie twórz drugi raz po restarcie
//      if (tenantRepository.existsByApiKey("testkey")) {
//        return;
//      }
//
//      // --- TENANT ---
//      Tenant tenant = Tenant.builder()
//          .name("Test Store")
//          .apiKey("testkey")
//          .createdAt(LocalDateTime.now())
//          .build();
//
//      tenantRepository.save(tenant);
//
//
//      // --- RULE ---
//      Rule rule = Rule.builder()
//          .tenantApiKey("testkey")
//          .eventName("order.paid")
//          .active(true)
//          .build();
//
//      ruleRepository.save(rule);
//
//
//      // --- ACTION ---
//      // 🔴 TU wstaw swój webhook.site / pipedream URL
//      String configJson = """
//                    {
//                      "url": "https://eo4v7n5h6y9v5k8.m.pipedream.net"
//                    }
//                    """;
//
//      Action action = Action.builder()
//          .rule(rule)
//          .type(ActionType.HTTP)
//          .config(configJson)
//          .build();
//
//      actionRepository.save(action);
//
//      System.out.println("=== SAMPLE DATA CREATED ===");
//      System.out.println("API KEY: testkey");
//      System.out.println("EVENT: order.paid");
//    };
//  }
}
