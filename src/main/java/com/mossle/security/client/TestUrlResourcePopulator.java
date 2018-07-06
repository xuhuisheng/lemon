package com.mossle.security.client;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import org.springframework.util.Assert;

public class TestUrlResourcePopulator {
    private static Logger logger = LoggerFactory
            .getLogger(TestUrlResourcePopulator.class);
    private FilterSecurityInterceptor filterSecurityInterceptor;
    private boolean enable = false;

    @PostConstruct
    public void init() {
        if (!enable) {
            logger.info("skip test url resource");

            return;
        }

        Map<String, String> resourceMap = new LinkedHashMap<String, String>();
        resourceMap.put("/common/login.jsp", "IS_GUEST");
        resourceMap.put("/**", "ROLE_USER");
        this.execute(resourceMap);
    }

    public void execute(Map<String, String> resourceMap) {
        Assert.notNull(filterSecurityInterceptor);
        Assert.notNull(resourceMap);

        logger.info("refresh url resource");

        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = null;
        requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();

        for (Map.Entry<String, String> entry : resourceMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            requestMap.put(new AntPathRequestMatcher(key),
                    SecurityConfig.createListFromCommaDelimitedString(value));
        }

        FilterInvocationSecurityMetadataSource source = new DefaultFilterInvocationSecurityMetadataSource(
                requestMap);
        filterSecurityInterceptor.setSecurityMetadataSource(source);
    }

    public void setFilterSecurityInterceptor(
            FilterSecurityInterceptor filterSecurityInterceptor) {
        this.filterSecurityInterceptor = filterSecurityInterceptor;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
