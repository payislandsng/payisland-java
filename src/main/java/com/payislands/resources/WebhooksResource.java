package com.payislands.resources;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class WebhooksResource {
    public boolean verifySignature(String payload, String signature, String secret) {
        if (payload == null) {
            return false;
        }

        return verifySignature(payload.getBytes(StandardCharsets.UTF_8), signature, secret);
    }

    public boolean verifySignature(byte[] payload, String signature, String secret) {
        if (payload == null || signature == null || signature.trim().isEmpty()
                || secret == null || secret.trim().isEmpty()) {
            return false;
        }

        try {
            String providedSignature = normalizeSignature(signature);
            String expectedSignature = hmacSha256Hex(payload, secret);

            return MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    providedSignature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception exception) {
            return false;
        }
    }

    private static String normalizeSignature(String signature) {
        String value = signature.trim();
        if (value.regionMatches(true, 0, "sha256=", 0, "sha256=".length())) {
            value = value.substring("sha256=".length());
        }
        return value.toLowerCase();
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
