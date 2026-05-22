import com.payisland.PayIsland;

public class WebhookVerificationExample {
    public static void main(String[] args) {
        String secretKey = requireEnv("PAYISLAND_SECRET_KEY");
        String webhookSecret = requireEnv("PAYISLAND_WEBHOOK_SECRET");

        PayIsland payIsland = new PayIsland(secretKey);
        String rawPayload = "{\"transaction_reference\":\"order_123\",\"status\":\"success\"}";
        String signature = "signature_from_payisland_header";

        boolean isValid = payIsland.webhooks().verifySignature(rawPayload, signature, webhookSecret);
        if (!isValid) {
            throw new IllegalStateException("Invalid PayIsland webhook signature");
        }

        System.out.println("Signature verified. Verify the transaction reference before fulfillment.");
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(name + " environment variable is required");
        }
        return value;
    }
}
