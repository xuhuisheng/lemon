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

/**
 * 不实际使用多租户的伪造版本.
 */
public class MockScopeFilter implements Filter {
    private String scopeCode = "default";
    private ScopeConnector scopeConnector;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            ScopeHolder.setScopeDto(scopeConnector.findByCode(scopeCode));

            request.setAttribute("scopePrefix", request.getContextPath());
            filterChain.doFilter(request, response);
        } finally {
            ScopeHolder.clear();
        }
    }

    // ~ ==================================================
    public void setScopeCode(String scopeCode) {
        this.scopeCode = scopeCode;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }
}
