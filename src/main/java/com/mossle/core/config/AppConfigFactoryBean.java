package com.mossle.core.config;

import org.springframework.beans.factory.FactoryBean;

public class AppConfigFactoryBean implements FactoryBean<AppConfig> {
    @Override
    public AppConfig getObject() throws Exception {
        return AppConfig.getInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return AppConfig.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
