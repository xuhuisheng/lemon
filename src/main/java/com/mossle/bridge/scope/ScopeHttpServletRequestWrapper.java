package com.mossle.bridge.scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ScopeHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private String requestUri;
    private String servletPath;
    private String oldServletPath;
    private String oldRequestUri;

    public ScopeHttpServletRequestWrapper(
            HttpServletRequest httpServletRequest, String scopeCode) {
        super(httpServletRequest);

        int index = scopeCode.length() + 1;
        String contextPath = httpServletRequest.getContextPath();
        this.requestUri = contextPath
                + httpServletRequest.getRequestURI().substring(
                        index + contextPath.length());
        this.servletPath = httpServletRequest.getServletPath().substring(index);
        this.oldServletPath = httpServletRequest.getServletPath();
        this.oldRequestUri = httpServletRequest.getRequestURI();
    }

    public String getRequestURI() {
        String newRequestUri = ((HttpServletRequest) getRequest())
                .getRequestURI();

        if (oldRequestUri.equals(newRequestUri)) {
            return requestUri;
        } else {
            return newRequestUri;
        }
    }

    public String getServletPath() {
        String newServletPath = ((HttpServletRequest) getRequest())
                .getServletPath();

        if (oldServletPath.equals(newServletPath)) {
            return servletPath;
        } else {
            return newServletPath;
        }
    }
}
