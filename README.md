# PayIsland Java SDK

Official Java SDK for integrating with PayIsland payment APIs.

## Installation

Add the SDK to your Maven project:

```xml
<dependency>
    <groupId>com.payisland</groupId>
    <artifactId>payisland-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

The SDK requires Java 11 or later.

## Initialization

```java
import com.payisland.PayIsland;

PayIsland payIsland = new PayIsland("test_secret_key");
```

You can override the base URL for tests:

```java
import com.payisland.PayIsland;
import com.payisland.PayIslandConfig;

PayIslandConfig config = new PayIslandConfig("test_secret_key", "http://localhost:8080");
PayIsland payIsland = new PayIsland(config);
```

PayIsland does not require a sandbox/live environment flag. The PayIsland backend determines the mode from the API key you provide.

## Transaction Initialization

```java
import com.payisland.PayIsland;

import java.util.Map;

PayIsland payIsland = new PayIsland("test_secret_key");

Map<String, Object> response = payIsland.transactions().initialize(Map.of(
    "callback_url", "https://example.com/webhooks/payislands",
    "payment_item_id", "6",
    "transaction_reference", "order_" + System.currentTimeMillis(),
    "channel", "card",
    "amount", "1000",
    "customer_info", Map.of(
        "email", "ada@example.com",
        "phone_number", "08011112222",
        "first_name", "Ada",
        "last_name", "Lovelace"
    )
));

System.out.println(response);
```

Card payments may require 3DS authentication. Treat an initialized card transaction as pending until the customer completes authentication and you verify the transaction status.

## Transaction Verification

```java
Map<String, Object> response = payIsland.transactions().verify("order_123");
System.out.println(response);
```

## Webhook Verification

```java
boolean isValid = payIsland.webhooks().verifySignature(
    rawPayload,
    signature,
    webhookSecret
);
```

Always verify the webhook signature before trusting the payload. Before fulfilling an order, verify the transaction reference with PayIsland using `transactions().verify(reference)`.

## Error Handling

API errors throw `PayIslandApiException`.

```java
import com.payisland.exceptions.PayIslandApiException;

try {
    payIsland.transactions().verify("order_123");
} catch (PayIslandApiException exception) {
    System.out.println(exception.getStatusCode());
    System.out.println(exception.getResponseBody());
    System.out.println(exception.getResponseData());
}
```

## Examples

See the `examples/` directory:

- `InitializePaymentExample.java`
- `VerifyPaymentExample.java`
- `WebhookVerificationExample.java`

## Development Commands

```bash
mvn test
mvn package
```

## License

MIT
