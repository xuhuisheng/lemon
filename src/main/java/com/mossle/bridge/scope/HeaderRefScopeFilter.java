package com.mossle.bridge.scope;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;

public class HeaderRefScopeFilter implements Filter {
    private String defaultScopeRef = "1";
    private ScopeConnector scopeConnector;
    private String scopeHeaderName = "x-scope-ref";

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    /**
     * <p>
     * http://localhost:8080/ctx/model/service.do
     * </p>
     * <p>
     * x-scope-ref: 1
     * </p>
     */
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String scopeRef = request.getHeader(scopeHeaderName);

        if (scopeRef == null) {
            scopeRef = defaultScopeRef;
        }

        try {
            ScopeHolder.setScopeDto(scopeConnector.findByRef(scopeRef));

            request.setAttribute("scopePrefix", request.getContextPath());
            filterChain.doFilter(request, response);
        } finally {
            ScopeHolder.clear();
        }
    }

    // ~ ==================================================
    public void setDefaultScopeRef(String defaultScopeRef) {
        this.defaultScopeRef = defaultScopeRef;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public void setScopeHeaderName(String scopeHeaderName) {
        this.scopeHeaderName = scopeHeaderName;
    }
}
