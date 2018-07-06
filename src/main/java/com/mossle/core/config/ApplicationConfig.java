package com.mossle.core.config;

import java.util.Properties;

public class ApplicationConfig {
    private Properties properties = new Properties();

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public int getInt(String key) {
        String value = properties.getProperty(key);

        try {
            return Integer.parseInt(value, 10);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public boolean getBoolean(String key) {
        String value = properties.getProperty(key);

        return Boolean.parseBoolean(value);
    }
}
