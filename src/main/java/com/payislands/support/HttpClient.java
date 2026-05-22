package com.payislands.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payislands.PayIslandConfig;
import com.payislands.exceptions.PayIslandApiException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;

public class HttpClient {
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final PayIslandConfig config;
    private final java.net.http.HttpClient client;
    private final ObjectMapper objectMapper;

    public HttpClient(PayIslandConfig config) {
        this.config = config;
        this.client = java.net.http.HttpClient.newBuilder()
                .connectTimeout(config.timeout())
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> post(String path, Map<String, Object> payload) {
        try {
            String body = objectMapper.writeValueAsString(payload == null ? Collections.emptyMap() : payload);
            HttpRequest request = baseRequest(path)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            return send(request);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Unable to serialize request payload", exception);
        }
    }

    public Map<String, Object> get(String path) {
        HttpRequest request = baseRequest(path)
                .GET()
                .build();

        return send(request);
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder(URI.create(config.baseUrl() + path))
                .timeout(config.timeout())
                .header("Authorization", "Bearer " + config.secretKey())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", "payisland-java");
    }

    private Map<String, Object> send(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body() == null ? "" : response.body();

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                try {
                    return parseBody(body);
                } catch (IOException exception) {
                    throw new PayIslandApiException(response.statusCode(), body, null, exception);
                }
            }

            throw new PayIslandApiException(response.statusCode(), body, parseBodyOrNull(body));
        } catch (IOException exception) {
            throw new PayIslandApiException(0, exception.getMessage(), null, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new PayIslandApiException(0, exception.getMessage(), null, exception);
        }
    }

    private Map<String, Object> parseBody(String body) throws IOException {
        if (body == null || body.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        return objectMapper.readValue(body, MAP_TYPE);
    }

    private Map<String, Object> parseBodyOrNull(String body) {
        try {
            return parseBody(body);
        } catch (IOException exception) {
            return null;
        }
    }
}
