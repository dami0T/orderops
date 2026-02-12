package pl.orderops.orderops.model;

public class WebhookHttpException extends RuntimeException {

  private final int statusCode;
  private final String responseBody;

  public WebhookHttpException(int statusCode, String responseBody) {
    super("Webhook HTTP error " + statusCode + ": " + responseBody);
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getResponseBody() {
    return responseBody;
  }
}
