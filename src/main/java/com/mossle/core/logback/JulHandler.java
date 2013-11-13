package com.mossle.core.logback;

import org.slf4j.bridge.SLF4JBridgeHandler;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class JulHandler implements InitializingBean, DisposableBean {
    public void afterPropertiesSet() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    public void destroy() {
        SLF4JBridgeHandler.uninstall();
    }
}
