package com.mossle.security.client;

import com.mossle.security.api.MethodSourceFetcher;
import com.mossle.security.api.UrlSourceFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

public class ResourceDetailsMonitor implements InitializingBean {
    private static Logger logger = LoggerFactory
            .getLogger(ResourceDetailsMonitor.class);
    private boolean debug;
    private UrlSourceBuilder urlSourceBuilder;
    private MethodSourceBuilder methodSourceBuilder;
    private UrlSourceFetcher urlSourceFetcher;
    private MethodSourceFetcher methodSourceFetcher;
    private DelegatingMethodSecurityMetadataSource delegatingMethodSecurityMetadataSource;
    private FilterSecurityInterceptor filterSecurityInterceptor;
    private long lastModified;

    public void afterPropertiesSet() {
        if (urlSourceFetcher != null) {
            urlSourceBuilder = new UrlSourceBuilder();
            urlSourceBuilder.setUrlSourceFetcher(urlSourceFetcher);
        }

        if (urlSourceBuilder != null) {
            urlSourceBuilder
                    .setFilterSecurityInterceptor(filterSecurityInterceptor);
        }

        if (methodSourceFetcher != null) {
            methodSourceBuilder = new MethodSourceBuilder();
            methodSourceBuilder.setMethodSourceFetcher(methodSourceFetcher);
        }

        if (methodSourceBuilder != null) {
            methodSourceBuilder
                    .setDelegatingMethodSecurityMetadataSource(delegatingMethodSecurityMetadataSource);
        }

        refresh();
    }

    public void refresh() {
        if (debug) {
            logger.info("debug mode, skip refresh");

            return;
        }

        logger.info("execute refresh");

        if (urlSourceBuilder != null) {
            urlSourceBuilder.refresh();
        }

        if (methodSourceBuilder != null) {
            methodSourceBuilder.refresh();
        }

        this.lastModified = System.currentTimeMillis();
    }

    // ~ ======================================================================
    public long getLastModified() {
        return lastModified;
    }

    // ~ ======================================================================
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setUrlSourceBuilder(UrlSourceBuilder urlSourceBuilder) {
        this.urlSourceBuilder = urlSourceBuilder;
    }

    public void setMethodSourceBuilder(MethodSourceBuilder methodSourceBuilder) {
        this.methodSourceBuilder = methodSourceBuilder;
    }

    public void setUrlSourceFetcher(UrlSourceFetcher urlSourceFetcher) {
        this.urlSourceFetcher = urlSourceFetcher;
    }

    public void setMethodSourceFetcher(MethodSourceFetcher methodSourceFetcher) {
        this.methodSourceFetcher = methodSourceFetcher;
    }

    public void setDelegatingMethodSecurityMetadataSource(
            DelegatingMethodSecurityMetadataSource delegatingMethodSecurityMetadataSource) {
        this.delegatingMethodSecurityMetadataSource = delegatingMethodSecurityMetadataSource;
    }

    public void setFilterSecurityInterceptor(
            FilterSecurityInterceptor filterSecurityInterceptor) {
        this.filterSecurityInterceptor = filterSecurityInterceptor;
    }
}
