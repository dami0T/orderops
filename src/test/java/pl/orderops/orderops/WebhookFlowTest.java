package pl.orderops.orderops;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.orderops.orderops.ruleengine.model.Rule;
import pl.orderops.orderops.ruleengine.model.RuleRepository;
import pl.orderops.orderops.model.tenant.Tenant;
import pl.orderops.orderops.model.tenant.TenantRepository;
import pl.orderops.orderops.service.webhook.WebhookService;


@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
class WebhookFlowTest {

  @Autowired
  private TenantRepository tenantRepository;

  @Autowired
  private RuleRepository ruleRepository;


  @Autowired
  private WebhookService webhookService;

  @Autowired
  private ObjectMapper objectMapper;

  private MockWebServer mockWebServer;

  private Tenant tenant;
  private Rule rule;


//  @BeforeEach
//  void setup() throws Exception {
//    // Czyścimy bazę przed każdym testem
//    actionRepository.deleteAll();
//    ruleRepository.deleteAll();
//    webhookRepository.deleteAll();
//    tenantRepository.deleteAll();
//
//    // Uruchamiamy mock server
//    mockWebServer = new MockWebServer();
//    mockWebServer.start();
//    mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("OK"));
//
//    // Tworzymy tenant z unikalnym API key
//    String apiKey = "testkey-" + System.currentTimeMillis();
//    tenant = tenantRepository.save(Tenant.builder()
//        .name("Test Store")
//        .apiKey(apiKey)
//        .build());
//
//    // Tworzymy regułę
//    rule = ruleRepository.save(Rule.builder()
//        .tenantApiKey(tenant.getApiKey())
//        .eventName("order.paid")
//        .active(true)
//        .build());
//
//    // Tworzymy akcję HTTP z JSON string w config
//    action = actionRepository.save(Action.builder()
//        .rule(rule)
//        .type(ActionType.HTTP)
//        .config(objectMapper.writeValueAsString(Map.of(
//            "url", mockWebServer.url("/webhook").toString()
//        )))
//        .build());
//  }
//
//  @AfterEach
//  void teardown() throws Exception {
//    if (mockWebServer != null) {
//      mockWebServer.shutdown();
//    }
//  }
//
//  @Test
//  void testWebhookFlow() throws Exception {
//    String payload = "{\"event\":\"order.paid\",\"data\":{\"orderId\":123}}";
//
//    // Wywołujemy webhook service
//    webhookService.saveEvent(tenant.getApiKey(), payload);
//
//    // Czekamy na request w mock server
//    var request = mockWebServer.takeRequest();
//    assertNotNull(request, "Webhook was not sent!");
//    Assertions.assertEquals("/webhook", request.getPath());
//    Assertions.assertEquals(payload, request.getBody().readUtf8());
//  }
}