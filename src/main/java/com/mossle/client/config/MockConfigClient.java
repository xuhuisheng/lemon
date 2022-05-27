package com.mossle.client.config;

public class MockConfigClient implements ConfigClient {
    public Config getConfig() {
        return new MockConfig();
    }
}
