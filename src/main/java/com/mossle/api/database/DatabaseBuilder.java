package com.mossle.api.database;

import java.util.Properties;

public abstract class DatabaseBuilder<T extends DatabaseBuilder<T>> {
    private Properties properties;
    private String defaultPrefix;

    public static DatabaseBuilder<?> newBuilder() {
        return DatabaseProvider.provider().newBuilder();
    }

    public Properties getProperties() {
        return properties;
    }

    public DatabaseBuilder<?> setProperties(Properties properties) {
        this.properties = properties;

        return this;
    }

    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    public DatabaseBuilder<?> setDefaultPrefix(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;

        return this;
    }

    public abstract Database build();
}
