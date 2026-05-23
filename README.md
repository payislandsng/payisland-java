# PayIsland Java SDK

![CI](https://github.com/payislandsng/payisland-java/actions/workflows/ci.yml/badge.svg)

Official Java SDK for integrating with PayIsland payment APIs.

## Installation

Add the SDK to your Maven project:

```xml
<dependency>
    <groupId>com.payislands</groupId>
    <artifactId>payisland-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

The SDK requires Java 11 or later.

## Initialization

```java
import com.payislands.PayIsland;

PayIsland payIsland = new PayIsland("test_secret_key");
```

You can override the base URL for tests:

```java
import com.payislands.PayIsland;
import com.payislands.PayIslandConfig;

PayIslandConfig config = new PayIslandConfig("test_secret_key", "http://localhost:8080");
PayIsland payIsland = new PayIsland(config);
```

PayIsland does not require a sandbox/live environment flag. The PayIsland backend determines the mode from the API key you provide.

## Transaction Initialization

```java
import com.payislands.PayIsland;

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
import com.payislands.exceptions.PayIslandApiException;

try {
    payIsland.transactions().verify("order_123");
} catch (PayIslandApiException exception) {
    System.out.println(exception.getStatusCode());
    System.out.println(exception.getResponseBody());
    System.out.println(exception.getResponseData());
}
```

## Examples

The runnable examples live in `src/test/java/com/payislands/examples` so Maven can compile them onto the test classpath.
On a clean checkout, run `mvn test-compile` once before using `exec:java`, or run these commands after `mvn test` or `mvn package`.

Initialize a payment:

```bash
mvn exec:java -Dexec.mainClass="com.payislands.examples.InitializePaymentExample" -Dexec.classpathScope=test
```

Verify a payment:

```bash
mvn exec:java -Dexec.mainClass="com.payislands.examples.VerifyPaymentExample" -Dexec.args="<reference>" -Dexec.classpathScope=test
```

Verify a webhook signature:

```bash
mvn exec:java -Dexec.mainClass="com.payislands.examples.WebhookVerificationExample" -Dexec.classpathScope=test
```

## Development Commands

```bash
mvn test
mvn package
```

## Publishing to Maven Central

Run the local release checks:

```bash
mvn clean verify
```

Publish through the Sonatype Central Portal release profile:

```bash
mvn clean deploy -P release
```

Before deploying, configure `~/.m2/settings.xml` with Central Portal token credentials using the server id `central`. Generate the token in the Central Portal, then add it as a Maven server entry:

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>your_token_username</username>
      <password>your_token_password</password>
    </server>
  </servers>
</settings>
```

Do not commit real credentials. The release profile signs artifacts with GPG and publishes the deployment bundle to the Central Portal for validation.

## License

MIT
