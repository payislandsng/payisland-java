package com.payisland;

import com.payisland.resources.TransactionsResource;
import com.payisland.resources.WebhooksResource;
import com.payisland.support.HttpClient;

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
