package com.payislands;

import java.time.Duration;
import java.util.Objects;

public class PayIslandConfig {
    public static final String DEFAULT_BASE_URL = "https://ags.payislands.com";
    public static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private final String secretKey;
    private final String baseUrl;
    private final int timeoutSeconds;

    public PayIslandConfig(String secretKey) {
        this(secretKey, DEFAULT_BASE_URL, DEFAULT_TIMEOUT_SECONDS);
    }

    public PayIslandConfig(String secretKey, String baseUrl) {
        this(secretKey, baseUrl, DEFAULT_TIMEOUT_SECONDS);
    }

    public PayIslandConfig(String secretKey, String baseUrl, int timeoutSeconds) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("secretKey is required");
        }
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("timeoutSeconds must be greater than zero");
        }

        this.secretKey = secretKey.trim();
        this.baseUrl = normalizeBaseUrl(Objects.requireNonNullElse(baseUrl, DEFAULT_BASE_URL));
        this.timeoutSeconds = timeoutSeconds;
    }

    public String secretKey() {
        return secretKey;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public int timeoutSeconds() {
        return timeoutSeconds;
    }

    public Duration timeout() {
        return Duration.ofSeconds(timeoutSeconds);
    }

    private static String normalizeBaseUrl(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return DEFAULT_BASE_URL;
        }
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
