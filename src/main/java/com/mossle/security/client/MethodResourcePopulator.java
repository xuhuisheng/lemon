package com.mossle.security.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mossle.core.util.BeanUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

import org.springframework.util.Assert;

public class MethodResourcePopulator {
    private static Logger logger = LoggerFactory
            .getLogger(MethodResourcePopulator.class);

    public void execute(
            DelegatingMethodSecurityMetadataSource delegatingMethodSecurityMetadataSource,
            Map<String, String> resourceMap) {
        Assert.notNull(delegatingMethodSecurityMetadataSource);
        Assert.notNull(resourceMap);

        logger.info("refresh method resource");

        Map<String, List<ConfigAttribute>> methodMap = null;
        methodMap = new LinkedHashMap<String, List<ConfigAttribute>>();

        for (Map.Entry<String, String> entry : resourceMap.entrySet()) {
            methodMap.put(entry.getKey(), SecurityConfig
                    .createListFromCommaDelimitedString(entry.getValue()));
        }

        MethodSecurityMetadataSource source = new MapBasedMethodSecurityMetadataSource(
                methodMap);
        List<MethodSecurityMetadataSource> sources = new ArrayList<MethodSecurityMetadataSource>();
        sources.add(source);

        List<MethodSecurityMetadataSource> methodSecurityMetadataSources = delegatingMethodSecurityMetadataSource
                .getMethodSecurityMetadataSources();
        methodSecurityMetadataSources.clear();
        methodSecurityMetadataSources.addAll(sources);

        Map attributeCache = (Map) BeanUtils.safeGetFieldValue(
                delegatingMethodSecurityMetadataSource, "attributeCache");
        attributeCache.clear();
    }
}
