package com.payisland.examples;

import com.payisland.PayIsland;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class WebhookVerificationExample {
    public static void main(String[] args) throws Exception {
        PayIsland payIsland = new PayIsland("test_secret_key");
        String webhookSecret = "webhook_secret";
        String rawPayload = "{\"transaction_reference\":\"order_123\",\"status\":\"success\"}";
        String signature = hmacSha256Hex(rawPayload.getBytes(StandardCharsets.UTF_8), webhookSecret);

        boolean isValid = payIsland.webhooks().verifySignature(rawPayload, signature, webhookSecret);
        System.out.println("Signature valid: " + isValid);
        System.out.println("Always verify the transaction reference before fulfillment.");
    }

    private static String hmacSha256Hex(byte[] payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(payload);

        StringBuilder builder = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
