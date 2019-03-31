package com.mossle.core.config;

import java.io.IOException;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// app-cluster-env.properties
public final class AppConfig {
    private static Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static AppConfig instance = new AppConfig();
    private Properties properties = new Properties();
    private ConfigMetadata configMetadata;

    private AppConfig() {
        try {
            init();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static AppConfig getInstance() {
        return instance;
    }

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

    public Object getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void init() throws Exception {
        this.initConfigMetadata();
        this.initAppConfig();
    }

    public void initConfigMetadata() throws IOException {
        Properties prop = new Properties();
        prop.load(AppConfig.class.getClassLoader().getResourceAsStream(
                "bootstrap.properties"));
        configMetadata = new ConfigMetadata();

        if (prop.containsKey("app.id")) {
            configMetadata.setApplication(prop.getProperty("app.id"));
        }

        if (prop.containsKey("app.cluster")) {
            configMetadata.setCluster(prop.getProperty("app.cluster"));
        }

        if (prop.containsKey("app.env")) {
            configMetadata.setEnvironment(prop.getProperty("app.env"));
        }

        if (prop.containsKey("app.namespace")) {
            configMetadata
                    .setNamespace(prop.getProperty("app.namespace"));
        }

        logger.info("config app.id : {}", configMetadata.getApplication());
        logger.info("config app.cluster : {}", configMetadata.getCluster());
        logger.info("config app.env : {}", configMetadata.getEnvironment());
        logger.info("config app.namespace : {}", configMetadata.getNamespace());
    }

    public void initAppConfig() throws IOException {
        // String resourceName = configMetadata.getApplication() + "-"
        // + configMetadata.getCluster() + "-"
        // + configMetadata.getNamespace() + "-"
        // + configMetadata.getEnvironment() + ".properties";
        String resourceName = configMetadata.getNamespace() + "-"
                + configMetadata.getEnvironment() + ".properties";
        logger.info("config resourceName : {}", resourceName);

        Properties properties = new Properties();
        properties.load(AppConfig.class.getClassLoader().getResourceAsStream(
                resourceName));

        this.setProperties(properties);
    }

    public Properties getProperties() {
        return properties;
    }
}
