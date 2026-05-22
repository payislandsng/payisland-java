package com.payislands.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.payislands.PayIsland;

import java.util.Map;

public class VerifyPaymentExample {
    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args[0].trim().isEmpty()) {
            System.err.println("Usage: mvn exec:java -Dexec.mainClass=\"com.payislands.examples.VerifyPaymentExample\" -Dexec.args=\"<reference>\" -Dexec.classpathScope=test");
            return;
        }

        String secretKey = env("PAYISLAND_SECRET_KEY");
        if (secretKey == null) {
            System.err.println("Missing required environment variable: PAYISLAND_SECRET_KEY");
            return;
        }

        PayIsland payIsland = new PayIsland(secretKey);
        Map<String, Object> response = payIsland.transactions().verify(args[0]);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println(objectMapper.writeValueAsString(response));
    }

    private static String env(String name) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
