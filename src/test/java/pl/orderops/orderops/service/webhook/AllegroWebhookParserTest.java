package pl.orderops.orderops.service.webhook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.orderops.orderops.model.webhook.CanonicalWebhookEvent;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AllegroWebhookParserTest {

    private AllegroWebhookParser parser;

    @BeforeEach
    void setUp() {
        parser = new AllegroWebhookParser();
    }

    @Test
    void supports_allegro_returns_true() {
        assertTrue(parser.supports("allegro"));
        assertTrue(parser.supports("ALLEGRO"));
        assertTrue(parser.supports("AlLeGrO"));
    }

    @Test
    void supports_non_allegro_returns_false() {
        assertFalse(parser.supports("stripe"));
        assertFalse(parser.supports("shopify"));
        assertFalse(parser.supports(null));
    }

    @Test
    void parse_valid_payload_returns_canonical_event() {
        String payload = """
            {
                "id": "event-123",
                "type": "order.created",
                "message": "Test order"
            }
            """;

        CanonicalWebhookEvent result = parser.parse(payload, Map.of());

        assertEquals("allegro", result.getProvider());
        assertEquals("event-123", result.getExternalEventId());
        assertEquals("order.created", result.getEventType());
        assertEquals(payload, result.getPayload());
    }

    @Test
    void parse_invalid_json_throws_exception() {
        String invalidPayload = "{ invalid json }";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            parser.parse(invalidPayload, Map.of())
        );
        assertTrue(exception.getMessage().contains("Invalid Allegro payload"));
    }
}
