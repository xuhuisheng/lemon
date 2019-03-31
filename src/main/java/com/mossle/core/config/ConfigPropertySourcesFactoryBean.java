package com.mossle.core.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.FactoryBean;

import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySources;

public class ConfigPropertySourcesFactoryBean implements
        FactoryBean<PropertySources> {
    private MutablePropertySources propertySources;

    public void init() {
        AppConfig appConfig = AppConfig.getInstance();
        propertySources = new MutablePropertySources();
        propertySources.addFirst(new ConfigPropertySource(appConfig));
    }

    @Override
    public PropertySources getObject() throws Exception {
        if (propertySources == null) {
            this.init();
        }

        return propertySources;
    }

    @Override
    public Class<?> getObjectType() {
        return PropertySources.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
