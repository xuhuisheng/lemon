package com.mossle.security.client;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.client.authz.AuthzResourceClient;

import com.mossle.spi.security.ResourceDetailsRefresher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class DefaultResourceDetailsRefresher implements
        ResourceDetailsRefresher {
    private static Logger logger = LoggerFactory
            .getLogger(DefaultResourceDetailsRefresher.class);
    private ResourceDetailsMonitor resourceDetailsMonitor;
    private AuthzResourceClient authzResourceClient;
    private String sysCode;
    private boolean debug;

    @PostConstruct
    public void refresh() {
        if (debug) {
            logger.info("debug mode, skip refresh");

            return;
        }

        logger.info("execute refresh");

        resourceDetailsMonitor.updateUrlSource(authzResourceClient
                .findResourceByType("URL", sysCode));
        resourceDetailsMonitor.updateMethodSource(authzResourceClient
                .findResourceByType("METHOD", sysCode));
    }

    @Resource
    public void setResourceDetailsMonitor(
            ResourceDetailsMonitor resourceDetailsMonitor) {
        this.resourceDetailsMonitor = resourceDetailsMonitor;
    }

    @Resource
    public void setAuthzResourceClient(AuthzResourceClient authzResourceClient) {
        this.authzResourceClient = authzResourceClient;
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
