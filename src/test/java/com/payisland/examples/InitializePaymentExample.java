package com.payisland.examples;

import com.payisland.PayIsland;

import java.util.Map;

public class InitializePaymentExample {
    public static void main(String[] args) {
        String secretKey = env("PAYISLAND_SECRET_KEY");
        String paymentItemId = env("PAYISLAND_PAYMENT_ITEM_ID");

        if (secretKey == null || paymentItemId == null) {
            System.err.println("Missing required environment variables.");
            System.err.println("Set PAYISLAND_SECRET_KEY and PAYISLAND_PAYMENT_ITEM_ID, then try again.");
            return;
        }

        PayIsland payIsland = new PayIsland(secretKey);
        Map<String, Object> response = payIsland.transactions().initialize(Map.of(
                "callback_url", "https://example.com/webhooks/payislands",
                "payment_item_id", paymentItemId,
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

        Object authorizationUrl = authorizationUrl(response);
        if (authorizationUrl != null) {
            System.out.println(authorizationUrl);
        } else {
            System.out.println(response);
        }
    }

    private static String env(String name) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    @SuppressWarnings("unchecked")
    private static Object authorizationUrl(Map<String, Object> response) {
        Object direct = response.get("authorization_url");
        if (direct != null) {
            return direct;
        }

        Object data = response.get("data");
        if (data instanceof Map) {
            return ((Map<String, Object>) data).get("authorization_url");
        }
        return null;
    }
}
