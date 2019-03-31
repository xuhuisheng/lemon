package com.mossle.core.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.FactoryBean;

import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;

public class ConfigPropertySource extends PropertySource {
    private static Logger logger2 = LoggerFactory
            .getLogger(ConfigPropertySource.class);
    public static final String PROPERTY_SOURCE_NAME = "mossle-config";
    private AppConfig appConfig;

    public ConfigPropertySource(AppConfig appConfig) {
        super(PROPERTY_SOURCE_NAME);
        this.appConfig = appConfig;
    }

    public Object getProperty(String name) {
        logger2.debug("{} {}", name, appConfig.getProperty(name));

        return appConfig.getProperty(name);
    }
}
