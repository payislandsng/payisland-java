package com.payislands;

import com.payislands.exceptions.PayIslandApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionsResourceTest {
    private MockWebServer server;
    private PayIsland payIsland;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        payIsland = new PayIsland(new PayIslandConfig("test_secret_key", server.url("/").toString()));
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void initializeSendsPostToExpectedPath() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"success\",\"authorization_url\":\"https://checkout.test/pay\"}"));

        Map<String, Object> response = payIsland.transactions().initialize(Map.of(
                "amount", "1000",
                "channel", "card"
        ));

        RecordedRequest request = server.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/v1/transactions/in/initialize", request.getPath());
        assertEquals("Bearer test_secret_key", request.getHeader("Authorization"));
        assertEquals("application/json", request.getHeader("Content-Type"));
        assertEquals("application/json", request.getHeader("Accept"));
        assertEquals("payisland-java", request.getHeader("User-Agent"));
        assertEquals("success", response.get("status"));
        assertEquals("https://checkout.test/pay", response.get("authorization_url"));
        assertNotNull(request.getBody().readUtf8());
    }

    @Test
    void verifySendsGetToExpectedPath() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"paid\",\"reference\":\"order_123\"}"));

        Map<String, Object> response = payIsland.transactions().verify("order_123");

        RecordedRequest request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/v1/transactions/in/check-transaction-status/order_123", request.getPath());
        assertEquals("paid", response.get("status"));
        assertEquals("order_123", response.get("reference"));
    }

    @Test
    void apiErrorRaisesPayIslandApiException() {
        server.enqueue(new MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"bad request\"}"));

        PayIslandApiException exception = assertThrows(
                PayIslandApiException.class,
                () -> payIsland.transactions().initialize(Map.of("amount", "1000"))
        );

        assertEquals(400, exception.getStatusCode());
        assertEquals("{\"message\":\"bad request\"}", exception.getResponseBody());
        assertNotNull(exception.getResponseData());
        assertEquals("bad request", exception.getResponseData().get("message"));
    }
}
