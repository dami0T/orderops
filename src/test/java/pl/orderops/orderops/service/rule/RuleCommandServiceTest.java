package pl.orderops.orderops.service.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.orderops.orderops.action.model.ActionCreateRequest;
import pl.orderops.orderops.action.model.ActionType;
import pl.orderops.orderops.model.tenant.Tenant;
import pl.orderops.orderops.model.tenant.TenantRepository;
import pl.orderops.orderops.ruleengine.cache.RuleCacheRefresher;
import pl.orderops.orderops.ruleengine.model.Rule;
import pl.orderops.orderops.ruleengine.model.RuleCreateRequest;
import pl.orderops.orderops.ruleengine.model.RuleRepository;
import pl.orderops.orderops.ruleengine.model.RuleResponse;
import pl.orderops.orderops.ruleengine.model.RuleRevision;
import pl.orderops.orderops.ruleengine.model.RuleRevisionRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RuleCommandServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private RuleCacheRefresher cacheRefresher;

    @Mock
    private RuleRevisionRepository revisionRepository;

    @Mock
    private TenantRepository tenantRepository;

    private RuleCommandService service;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        service = new RuleCommandService(ruleRepository, cacheRefresher, 
                revisionRepository, tenantRepository, objectMapper);
    }

    @Test
    void create_valid_request_returns_response_and_saves_rule() {
        Long tenantId = 1L;
        Tenant tenant = Tenant.builder().id(tenantId).name("Test Tenant").build();
        
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(ruleRepository.save(any(Rule.class))).thenAnswer(inv -> {
            Rule rule = inv.getArgument(0);
            rule.setId(100L);
            return rule;
        });
        when(revisionRepository.save(any(RuleRevision.class))).thenAnswer(inv -> {
            RuleRevision rev = inv.getArgument(0);
            rev.setId(200L);
            return rev;
        });

        RuleCreateRequest request = new RuleCreateRequest();
        request.setTenantId(tenantId);
        request.setEventType("order.created");
        request.setTrigger(Map.of("event", "order.created"));
        request.setCondition(Map.of("field", "status", "equals", "paid"));
        
        ActionCreateRequest action = new ActionCreateRequest();
        action.setType(ActionType.HTTP);
        action.setConfig(Map.of("url", "http://example.com"));
        request.setActions(List.of(action));

        RuleResponse response = service.create(request);

        assertNotNull(response);
        assertEquals(Long.valueOf(100L), response.id());
        assertEquals("order.created", response.eventType());
        assertTrue(response.active());

        verify(ruleRepository).save(any(Rule.class));
        verify(revisionRepository).save(any(RuleRevision.class));
        verify(cacheRefresher).refresh();
    }

    @Test
    void create_tenant_not_found_throws_exception() {
        Long tenantId = 999L;
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        RuleCreateRequest request = new RuleCreateRequest();
        request.setTenantId(tenantId);
        request.setEventType("order.created");

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    @Test
    void create_saves_rule_with_correct_data() {
        Long tenantId = 1L;
        Tenant tenant = Tenant.builder().id(tenantId).name("Test Tenant").build();
        
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(ruleRepository.save(any(Rule.class))).thenAnswer(inv -> {
            Rule rule = inv.getArgument(0);
            rule.setId(100L);
            return rule;
        });
        when(revisionRepository.save(any(RuleRevision.class))).thenAnswer(inv -> inv.getArgument(0));

        RuleCreateRequest request = new RuleCreateRequest();
        request.setTenantId(tenantId);
        request.setEventType("order.created");
        request.setTrigger(Map.of("event", "order.created", "source", "allegro"));

        service.create(request);

        ArgumentCaptor<Rule> ruleCaptor = ArgumentCaptor.forClass(Rule.class);
        verify(ruleRepository).save(ruleCaptor.capture());

        Rule savedRule = ruleCaptor.getValue();
        assertEquals("order.created", savedRule.getName());
        assertEquals(tenant, savedRule.getTenant());
        assertTrue(savedRule.isActive());
    }
}
