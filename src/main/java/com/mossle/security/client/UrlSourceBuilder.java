package com.mossle.security.client;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mossle.security.api.UrlSourceFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @deprecated use UrlResourcePopulator instead.
 */
@Deprecated
public class UrlSourceBuilder {
    private static Logger logger = LoggerFactory
            .getLogger(UrlSourceBuilder.class);
    private FilterSecurityInterceptor filterSecurityInterceptor;
    private UrlSourceFetcher urlSourceFetcher;

    public void refresh() {
        if ((filterSecurityInterceptor == null) || (urlSourceFetcher == null)) {
            logger.info(
                    "filterSecurityInterceptor : {}, urlSourceFetcher : {}",
                    filterSecurityInterceptor, urlSourceFetcher);

            return;
        }

        logger.info("execute refresh");

        Map<String, String> resourceMap = urlSourceFetcher.getSource(null);

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

    public void setUrlSourceFetcher(UrlSourceFetcher urlSourceFetcher) {
        this.urlSourceFetcher = urlSourceFetcher;
    }
}
