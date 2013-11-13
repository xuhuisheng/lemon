package com.mossle.core.util;

import org.dozer.DozerBeanMapperSingletonWrapper;

import org.springframework.beans.factory.InitializingBean;

public class DozerInitializer implements InitializingBean {
    public void afterPropertiesSet() {
        DozerBeanMapperSingletonWrapper.getInstance();
    }
}
