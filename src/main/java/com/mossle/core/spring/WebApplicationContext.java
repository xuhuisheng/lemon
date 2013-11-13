package com.mossle.core.spring;

import org.springframework.core.env.ConfigurableEnvironment;

import org.springframework.web.context.support.XmlWebApplicationContext;

public class WebApplicationContext extends XmlWebApplicationContext {
    @Override
    public ConfigurableEnvironment createEnvironment() {
        return new WebEnvironment();
    }
}
