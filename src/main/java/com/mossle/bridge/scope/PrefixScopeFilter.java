package com.mossle.bridge.scope;

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

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeDTO;
import com.mossle.api.scope.ScopeHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefixScopeFilter implements Filter {
    private static Logger logger = LoggerFactory
            .getLogger(PrefixScopeFilter.class);
    private Set<String> excludes = new HashSet<String>();
    private String defaultScopeCode = "default";
    private ScopeConnector scopeConnector;
    private ScopeDTO mockScopeDto = new ScopeDTO();

    public PrefixScopeFilter() {
        excludes.add("s");
        excludes.add("common");
        excludes.add("rs");
        excludes.add("gef");
        excludes.add("xform");
        excludes.add("h2database");
        excludes.add("widgets");
        excludes.add("j_spring_security_logout");
        excludes.add("j_spring_security_switch_user");
        excludes.add("j_spring_security_exit_user");
        excludes.add("favicon.ico");
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    /**
     * http://localhost:8080/ctx/default/model/service.do
     */
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String ctx = request.getContextPath();
        String requestUri = request.getRequestURI();

        String uri = requestUri.substring(ctx.length());
        String scopeCode = this.getScopeFromUri(uri);

        if ("".equals(scopeCode)) {
            // 如果是根目录，直接附加defaultScopeCode，并重定向
            this.jumpToDefaultPage(request, response, defaultScopeCode);
        } else {
            // 如果能获得scope，再继续判断
            if (excludes.contains(scopeCode)) {
                // 如果在排除列表内，直接忽略
                logger.debug("skip : {}, scopeCode : {}", uri, scopeCode);
                ScopeHolder.setScopeDto(mockScopeDto);
                this.doWithoutScope(request, response, filterChain);
            } else {
                // 如果不在排除列表内，对scope进行处理，继续执行
                String checkUri = uri.substring(scopeCode.length() + 1);

                // 如果访问的是http://localhost:8080/scopeCode或http://localhost:8080/scopeCode/
                // 因为服务器自动的forward会忽略wrapper，所以会导致找不到index.jsp
                // 这时手工跳转到dashboard/dashboard.jsp
                if ("".equals(checkUri) || "/".equals(checkUri)) {
                    this.jumpToDefaultPage(request, response, scopeCode);
                } else {
                    String realUri = ctx + checkUri;
                    logger.debug("checkUri : {}, realUri : {}", checkUri,
                            realUri);
                    this.doWithScope(scopeCode, realUri, request, response,
                            filterChain);
                }
            }
        }
    }

    public void doWithScope(String scopeCode, String uri,
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            ScopeHolder.setScopeDto(scopeConnector.findByCode(scopeCode));

            request.setAttribute("scopePrefix", request.getContextPath() + "/"
                    + scopeCode);
            filterChain.doFilter(new ScopeHttpServletRequestWrapper(request,
                    scopeCode), new ScopeHttpServletResponseWrapper(request,
                    response, scopeCode));
        } finally {
            ScopeHolder.clear();
        }
    }

    public void doWithoutScope(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }

    public void jumpToDefaultPage(HttpServletRequest request,
            HttpServletResponse response, String scopeCode) throws IOException {
        response.sendRedirect(request.getContextPath() + "/" + scopeCode
                + "/index.do");
    }

    public String getScopeFromUri(String uri) {
        // http://localhost:8080/ctx会被服务器自动转换为http://localhost:8080/ctx/
        int scopeIndex = uri.indexOf('/', 1);

        if (scopeIndex > 0) {
            // http://localhost:8080/ctx/scope/something/的情况
            return uri.substring(1, scopeIndex);
        } else {
            // http://localhost:8080/ctx/的情况
            // http://localhost:8080/ctx/scope的情况
            return uri.substring(1);
        }
    }

    // ~ ==================================================
    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public void setDefaultScopeCode(String defaultScopeCode) {
        this.defaultScopeCode = defaultScopeCode;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }
}
