package com.mossle.core.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import org.springframework.web.context.support.StandardServletEnvironment;

public class WebEnvironment extends StandardServletEnvironment {
    private static Logger logger = LoggerFactory
            .getLogger(WebEnvironment.class);

    @Override
    protected void customizePropertySources(
            MutablePropertySources propertySources) {
        try {
            ApplicationPropertiesFactoryBean applicationPropertiesFactoryBean = new ApplicationPropertiesFactoryBean();
            applicationPropertiesFactoryBean.afterPropertiesSet();
            propertySources.addLast(new MapPropertySource(
                    "applicationProperties", applicationPropertiesFactoryBean
                            .getMap()));
        } catch (Exception ex) {
            logger.error("", ex);
        }

        super.customizePropertySources(propertySources);
    }
}
