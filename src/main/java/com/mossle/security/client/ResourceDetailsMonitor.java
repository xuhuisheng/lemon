package com.mossle.security.client;

import java.util.Map;

import com.mossle.security.api.MethodSourceFetcher;
import com.mossle.security.api.UrlSourceFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import org.springframework.util.Assert;

public class ResourceDetailsMonitor implements InitializingBean {
    private static Logger logger = LoggerFactory
            .getLogger(ResourceDetailsMonitor.class);
    private UrlSourceFetcher urlSourceFetcher;
    private MethodSourceFetcher methodSourceFetcher;
    private DelegatingMethodSecurityMetadataSource delegatingMethodSecurityMetadataSource;
    private FilterSecurityInterceptor filterSecurityInterceptor;
    private UrlResourcePopulator urlResourcePopulator = new UrlResourcePopulator();
    private MethodResourcePopulator methodResourcePopulator = new MethodResourcePopulator();
    private boolean debug;

    public void afterPropertiesSet() {
        Assert.notNull(urlSourceFetcher);
        Assert.notNull(methodSourceFetcher);
        Assert.notNull(filterSecurityInterceptor);
        Assert.notNull(delegatingMethodSecurityMetadataSource);

        refresh();
    }

    public void refresh() {
        if (debug) {
            logger.info("debug mode, skip refresh");

            return;
        }

        logger.info("execute refresh");

        Map<String, String> urlResourceMap = urlSourceFetcher.getSource(null);
        urlResourcePopulator.execute(filterSecurityInterceptor, urlResourceMap);

        Map<String, String> methodResourceMap = methodSourceFetcher
                .getSource(null);
        methodResourcePopulator.execute(delegatingMethodSecurityMetadataSource,
                methodResourceMap);
    }

    // ~ ======================================================================
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

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
