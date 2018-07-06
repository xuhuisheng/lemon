package com.mossle.cdn;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.servlet.Filter;

import com.mossle.core.servlet.BeforeInvocationFilter;
import com.mossle.core.servlet.ProxyFilter;
import com.mossle.core.servlet.ServletFilter;
import com.mossle.core.servlet.StaticContentFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class CdnServletRegister {
    private static Logger logger = LoggerFactory
            .getLogger(CdnServletRegister.class);
    private BeforeInvocationFilter beforeInvocationFilter;
    private String baseDir;
    private boolean enable = false;

    @PostConstruct
    public void init() {
        if (!enable) {
            logger.info("cdn disabled");

            return;
        } else {
            logger.info("cdn enabled");
        }

        // this.addFirstFilter("cdn-o", "/cdn/o/*", new CdnStaticContentFilter(
        // baseDir));
        // this.addFirstFilter("cdn-r", "/cdn/r/*", new CdnStaticContentFilter(
        // baseDir));
        this.addFirstFilter("cdn-public", "/cdn/public/*",
                new CdnStaticContentFilter(baseDir));
    }

    public void addFirstFilter(String name, String urlPattern, Filter filter) {
        logger.info("add first filter : {} {} {} ", name, urlPattern, filter);

        ProxyFilter proxyFilter = new ProxyFilter();
        proxyFilter.setName(name);
        proxyFilter.setFilter(filter);
        proxyFilter.setUrlPattern(urlPattern);

        this.beforeInvocationFilter.addFirstFilter(proxyFilter);
    }

    @Resource
    public void setBeforeInvocationFilter(
            BeforeInvocationFilter beforeInvocationFilter) {
        this.beforeInvocationFilter = beforeInvocationFilter;
    }

    @Value("${store.baseDir}")
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    @Value("${cdn.enable}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
