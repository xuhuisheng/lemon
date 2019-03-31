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

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentCachedFilter extends ProxyFilter {
    private static Logger logger = LoggerFactory
            .getLogger(ContentCachedFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(
                (HttpServletRequest) request);
        String body = new String(requestWrapper.getBody(),
                request.getCharacterEncoding());
        logger.debug("body : {}", body);
        requestWrapper.setAttribute("x-payload", body);

        chain.doFilter(requestWrapper, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // init
    }

    public void destroy() {
        // destroy
    }
}
