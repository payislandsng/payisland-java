package com.payislands;

import com.payislands.resources.TransactionsResource;
import com.payislands.resources.WebhooksResource;
import com.payislands.support.HttpClient;

public class PayIsland {
    private final PayIslandConfig config;
    private final TransactionsResource transactions;
    private final WebhooksResource webhooks;

    public PayIsland(String secretKey) {
        this(new PayIslandConfig(secretKey));
    }

    public PayIsland(PayIslandConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config is required");
        }

        this.config = config;
        HttpClient httpClient = new HttpClient(config);
        this.transactions = new TransactionsResource(httpClient);
        this.webhooks = new WebhooksResource();
    }

    public TransactionsResource transactions() {
        return transactions;
    }

    public WebhooksResource webhooks() {
        return webhooks;
    }

    public String baseUrl() {
        return config.baseUrl();
    }
}
