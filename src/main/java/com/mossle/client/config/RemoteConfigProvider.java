package com.mossle.client.config;

public interface RemoteConfigProvider {
    Config findConfigByApp(String appName);
}
