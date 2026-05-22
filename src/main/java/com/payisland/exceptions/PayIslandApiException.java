package com.payisland.exceptions;

import java.util.Map;

public class PayIslandApiException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;
    private final Map<String, Object> responseData;

    public PayIslandApiException(int statusCode, String responseBody, Map<String, Object> responseData) {
        super("PayIsland API request failed with status code " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.responseData = responseData;
    }

    public PayIslandApiException(int statusCode, String responseBody, Map<String, Object> responseData, Throwable cause) {
        super("PayIsland API request failed with status code " + statusCode, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.responseData = responseData;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Map<String, Object> getResponseData() {
        return responseData;
    }
}
