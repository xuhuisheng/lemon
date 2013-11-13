package com.mossle.core.scope;

import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(ScopeFilter.class);
    private Set<String> excludes = new HashSet<String>();
    private String defaultGlobalCode = "global";
    private String defaultLocalCode = "local";

    public ScopeFilter() {
        excludes.add("s");
        excludes.add("common");
        excludes.add("rs");
        excludes.add("gef");
        excludes.add("xform");
        excludes.add("h2database");
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * http://localhost:8080/ctx/global/local/model/service.do
     */
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String ctx = request.getContextPath();
        String requestUri = request.getRequestURI();
        String uri = requestUri.substring(ctx.length());
        int globalIndex = uri.indexOf('/', 1);
        int localIndex = uri.indexOf('/', globalIndex + 1);

        if (globalIndex > 0) {
            String globalCode = uri.substring(1, globalIndex);

            if (excludes.contains(globalCode)) {
                logger.debug("skip : {}, globalCode : {}", uri, globalCode);
                doWithoutScope(request, response, filterChain);

                return;
            }
        } else {
            String globalCode = uri.substring(1);

            if (excludes.contains(globalCode)) {
                logger.debug("skip : {}, globalCode : {}", uri, globalCode);
                doWithoutScope(request, response, filterChain);

                return;
            }

            logger.debug("skip : {}", uri);
            this.jumpToDefaultPage(request, response, defaultGlobalCode,
                    defaultLocalCode);

            return;
        }

        if ((globalIndex > 0) && (localIndex > 0)) {
            String globalCode = uri.substring(1, globalIndex);
            String localCode = uri.substring(globalIndex + 1, localIndex);
            String checkUri = uri.substring(localIndex);

            if ("/".equals(checkUri)) {
                logger.debug("redirect dashboard : {}", uri);
                this.jumpToDefaultPage(request, response, globalCode, localCode);

                return;
            }

            String realUri = ctx + checkUri;
            logger.debug("do with scope : {}", realUri);
            doWithScope(globalCode, localCode, realUri, request, response,
                    filterChain);
        } else {
            logger.debug("jump to default page : {}", uri);
            this.jumpToDefaultPage(request, response, defaultGlobalCode,
                    defaultLocalCode);
        }
    }

    public void doWithScope(String globalCode, String localCode, String uri,
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            ScopeHolder.setScope(globalCode, localCode);
            // System.out.println("start : " + appCode);
            // System.out.println("uri : " + uri);
            request.setAttribute("scopePrefix", request.getContextPath() + "/"
                    + globalCode + "/" + localCode);
            request.setAttribute("globalCode", globalCode);
            request.setAttribute("localCode", localCode);
            filterChain.doFilter(new ScopeHttpServletRequestWrapper(request,
                    globalCode, localCode),
                    new ScopeHttpServletResponseWrapper(request, response,
                            globalCode, localCode));
        } finally {
            ScopeHolder.clear();

            // System.out.println("end : " + appCode);
        }
    }

    public void doWithoutScope(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }

    public void jumpToDefaultPage(HttpServletRequest request,
            HttpServletResponse response, String globalCode, String localCode)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/" + globalCode + "/"
                + localCode + "/dashboard/dashboard.do");
    }

    public void destroy() {
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public void setDefaultGlobalCode(String defaultGlobalCode) {
        this.defaultGlobalCode = defaultGlobalCode;
    }

    public void setDefaultLocalCode(String defaultLocalCode) {
        this.defaultLocalCode = defaultLocalCode;
    }
}
