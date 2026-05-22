package com.payisland;

import com.payisland.resources.WebhooksResource;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebhooksResourceTest {
    private final WebhooksResource webhooks = new WebhooksResource();

    @Test
    void validWebhookSignatureReturnsTrue() throws Exception {
        String payload = "{\"transaction_reference\":\"order_123\",\"status\":\"success\"}";
        String secret = "webhook_secret";
        String signature = hmacSha256Hex(payload.getBytes(StandardCharsets.UTF_8), secret);

        assertTrue(webhooks.verifySignature(payload, signature, secret));
        assertTrue(webhooks.verifySignature(payload.getBytes(StandardCharsets.UTF_8), "sha256=" + signature, secret));
    }

    @Test
    void invalidWebhookSignatureReturnsFalse() {
        String payload = "{\"transaction_reference\":\"order_123\",\"status\":\"success\"}";

        assertFalse(webhooks.verifySignature(payload, "invalid_signature", "webhook_secret"));
    }

    @Test
    void mismatchedOrEmptySignatureReturnsFalse() throws Exception {
        String payload = "{\"transaction_reference\":\"order_123\",\"status\":\"success\"}";
        String signature = hmacSha256Hex(payload.getBytes(StandardCharsets.UTF_8), "webhook_secret");

        assertFalse(webhooks.verifySignature(payload, signature, "different_secret"));
        assertFalse(webhooks.verifySignature(payload, "", "webhook_secret"));
        assertFalse(webhooks.verifySignature(payload, "   ", "webhook_secret"));
        assertFalse(webhooks.verifySignature(payload, null, "webhook_secret"));
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
