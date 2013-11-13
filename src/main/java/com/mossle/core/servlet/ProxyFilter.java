package com.mossle.core.servlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyFilter implements Filter {
    private String name;
    private Filter filter;
    private UrlPatternMatcher urlPatternMatcher = UrlPatternMatcher.DEFAULT_MATCHER;
    private Map<String, String> map = Collections.EMPTY_MAP;
    private boolean enable = true;
    private List<UrlPatternMatcher> excludes = new ArrayList<UrlPatternMatcher>();

    public void destroy() {
        if (enable) {
            filter.destroy();
        }
    }

    public void init(FilterConfig config) throws ServletException {
        if (enable) {
            ProxyFilterConfig proxyFilterConfig = new ProxyFilterConfig(
                    config.getServletContext());
            proxyFilterConfig.setFilterName(name);
            proxyFilterConfig.setMap(map);
            filter.init(proxyFilterConfig);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (enable) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            String contextPath = req.getContextPath();
            String requestUri = req.getRequestURI();
            String path = requestUri.substring(contextPath.length());

            // 如果在黑名单中，直接略过
            if (isExcluded(path)) {
                chain.doFilter(request, response);

                return;
            }

            // 如果符合redirect规则，进行跳转
            if (urlPatternMatcher.shouldRedirect(path)) {
                res.sendRedirect(contextPath + path + "/");

                return;
            }

            // 如果都没问题，才会继续进行判断
            if (urlPatternMatcher.matches(path)) {
                filter.doFilter(request, response, chain);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    protected boolean isExcluded(String path) {
        for (UrlPatternMatcher exclude : excludes) {
            if (exclude.matches(path)) {
                return true;
            }
        }

        return false;
    }

    // ~ ==================================================
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPatternMatcher = UrlPatternMatcher.create(urlPattern);
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setExcludePatterns(List<String> excludePatterns) {
        for (String excludePattern : excludePatterns) {
            excludes.add(UrlPatternMatcher.create(excludePattern));
        }
    }
}
