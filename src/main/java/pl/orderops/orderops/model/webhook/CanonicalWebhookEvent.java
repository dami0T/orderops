package pl.orderops.orderops.model.webhook;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CanonicalWebhookEvent {

  private String provider;          // allegro / stripe / shopify
  private String externalEventId;   // id nadawcy
  private String eventName;         // ORDER_CREATED / invoice.paid
  private String payload;           // raw payload
}
