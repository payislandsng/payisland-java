package com.payisland;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayIslandTest {
    @Test
    void requiresSecretKey() {
        assertThrows(IllegalArgumentException.class, () -> new PayIsland(""));
        assertThrows(IllegalArgumentException.class, () -> new PayIsland(new PayIslandConfig("   ")));
    }

    @Test
    void usesDefaultBaseUrl() {
        PayIsland payIsland = new PayIsland("test_secret_key");

        assertEquals("https://ags.payislands.com", payIsland.baseUrl());
    }

    @Test
    void supportsCustomBaseUrlOverride() {
        PayIsland payIsland = new PayIsland(new PayIslandConfig("test_secret_key", "https://example.test/"));

        assertEquals("https://example.test", payIsland.baseUrl());
    }

    @Test
    void exposesResources() {
        PayIsland payIsland = new PayIsland("test_secret_key");

        assertNotNull(payIsland.transactions());
        assertNotNull(payIsland.webhooks());
    }
}
