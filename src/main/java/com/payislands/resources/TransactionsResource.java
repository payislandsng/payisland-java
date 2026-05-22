package com.payislands.resources;

import com.payislands.support.HttpClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TransactionsResource {
    private static final String INITIALIZE_PATH = "/api/v1/transactions/in/initialize";
    private static final String VERIFY_PATH = "/api/v1/transactions/in/check-transaction-status/";

    private final HttpClient httpClient;

    public TransactionsResource(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Map<String, Object> initialize(Map<String, Object> payload) {
        return httpClient.post(INITIALIZE_PATH, payload);
    }

    public Map<String, Object> verify(String reference) {
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("reference is required");
        }

        String encodedReference = URLEncoder.encode(reference, StandardCharsets.UTF_8).replace("+", "%20");
        return httpClient.get(VERIFY_PATH + encodedReference);
    }
}
