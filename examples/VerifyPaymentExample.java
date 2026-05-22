import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.payisland.PayIsland;

import java.util.Map;

public class VerifyPaymentExample {
    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args[0].trim().isEmpty()) {
            throw new IllegalArgumentException("Usage: VerifyPaymentExample <transaction_reference>");
        }

        String secretKey = requireEnv("PAYISLAND_SECRET_KEY");
        PayIsland payIsland = new PayIsland(secretKey);

        Map<String, Object> response = payIsland.transactions().verify(args[0]);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println(objectMapper.writeValueAsString(response));
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(name + " environment variable is required");
        }
        return value;
    }
}
