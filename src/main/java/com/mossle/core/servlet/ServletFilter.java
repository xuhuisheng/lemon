package com.mossle.core.servlet;

import java.io.IOException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletFilter extends ProxyFilter {
    private static Logger logger = LoggerFactory.getLogger(ServletFilter.class);
    private Map<UrlPatternMatcher, Servlet> servletMap = Collections.EMPTY_MAP;

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String requestUri = req.getRequestURI();
        String path = requestUri.substring(contextPath.length());
        logger.trace("path : {}", path);

        for (Map.Entry<UrlPatternMatcher, Servlet> entry : servletMap
                .entrySet()) {
            UrlPatternMatcher urlPatternMatcher = entry.getKey();

            // 如果符合redirect规则，进行跳转
            if (urlPatternMatcher.shouldRedirect(path)) {
                logger.trace("{} should redirect {}",
                        urlPatternMatcher.getUrlPattern(), urlPatternMatcher);

                String redirectUrl = contextPath + path + "/";
                logger.trace("redirect to : {}", path);
                res.sendRedirect(redirectUrl);

                return;
            }

            if (urlPatternMatcher.matches(path)) {
                logger.trace("{} match {}", urlPatternMatcher, path);

                PathHttpServletRequestWrapper requestWrapper = new PathHttpServletRequestWrapper(
                        req, urlPatternMatcher.getUrlPattern());
                Servlet servlet = entry.getValue();
                logger.trace("invoke {}", servlet);
                servlet.service(requestWrapper, response);

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        for (Map.Entry<UrlPatternMatcher, Servlet> entry : servletMap
                .entrySet()) {
            Servlet servlet = entry.getValue();
            servlet.init(new ProxyServletConfig(filterConfig
                    .getServletContext()));
        }
    }

    public void destroy() {
        for (Map.Entry<UrlPatternMatcher, Servlet> entry : servletMap
                .entrySet()) {
            Servlet servlet = entry.getValue();
            servlet.destroy();
        }
    }

    public void setServletMap(Map<String, Servlet> urlPatternMap) {
        servletMap = new LinkedHashMap<UrlPatternMatcher, Servlet>();

        for (Map.Entry<String, Servlet> entry : urlPatternMap.entrySet()) {
            UrlPatternMatcher urlPatternMatcher = UrlPatternMatcher
                    .create(entry.getKey());
            servletMap.put(urlPatternMatcher, entry.getValue());
        }
    }
}
