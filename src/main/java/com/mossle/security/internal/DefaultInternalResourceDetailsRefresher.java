package com.mossle.security.internal;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.security.client.ResourceDetailsMonitor;

import com.mossle.spi.security.InternalAuthzResourceClient;
import com.mossle.spi.security.ResourceDetailsRefresher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class DefaultInternalResourceDetailsRefresher implements
        ResourceDetailsRefresher {
    private static Logger logger = LoggerFactory
            .getLogger(DefaultInternalResourceDetailsRefresher.class);
    private ResourceDetailsMonitor resourceDetailsMonitor;
    private InternalAuthzResourceClient internalAuthzResourceClient;
    private String sysCode;
    private boolean debug;

    @PostConstruct
    public void refresh() {
        if (debug) {
            logger.info("debug mode, skip refresh");

            return;
        }

        logger.info("execute refresh");

        resourceDetailsMonitor.updateUrlSource(internalAuthzResourceClient
                .findResourceByType("URL", sysCode));
        resourceDetailsMonitor.updateMethodSource(internalAuthzResourceClient
                .findResourceByType("METHOD", sysCode));
    }

    @Resource
    public void setResourceDetailsMonitor(
            ResourceDetailsMonitor resourceDetailsMonitor) {
        this.resourceDetailsMonitor = resourceDetailsMonitor;
    }

    @Resource
    public void setInternalAuthzResourceClient(
            InternalAuthzResourceClient internalAuthzResourceClient) {
        this.internalAuthzResourceClient = internalAuthzResourceClient;
    }

    @Value("${security.resource.sysCode}")
    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    @Value("${security.resource.debug}")
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
