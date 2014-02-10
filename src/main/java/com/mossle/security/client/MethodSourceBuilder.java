package com.mossle.security.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mossle.core.util.BeanUtils;

import com.mossle.security.api.MethodSourceFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

/**
 * @deprecated use MethodResourcePopulator instead.
 */
@Deprecated
public class MethodSourceBuilder {
    private static Logger logger = LoggerFactory
            .getLogger(MethodSourceBuilder.class);
    private DelegatingMethodSecurityMetadataSource delegatingMethodSecurityMetadataSource;
    private MethodSourceFetcher methodSourceFetcher;

    public void refresh() {
        if ((delegatingMethodSecurityMetadataSource == null)
                || (methodSourceFetcher == null)) {
            logger.info(
                    "delegatingMethodSecurityMetadataSource : {}, methodSourceFetcher : {}",
                    delegatingMethodSecurityMetadataSource, methodSourceFetcher);

            return;
        }

        logger.info("execute refresh");

        Map<String, List<ConfigAttribute>> methodMap = null;
        methodMap = new LinkedHashMap<String, List<ConfigAttribute>>();

        Map<String, String> resourceMap = methodSourceFetcher.getSource(null);

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

    public void setDelegatingMethodSecurityMetadataSource(
            DelegatingMethodSecurityMetadataSource delegatingMethodSecurityMetadataSource) {
        this.delegatingMethodSecurityMetadataSource = delegatingMethodSecurityMetadataSource;
    }

    public void setMethodSourceFetcher(MethodSourceFetcher methodSourceFetcher) {
        this.methodSourceFetcher = methodSourceFetcher;
    }
}
