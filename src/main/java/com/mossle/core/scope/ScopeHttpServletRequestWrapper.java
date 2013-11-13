package com.mossle.core.scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ScopeHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private String requestUri;
    private String servletPath;
    private String oldServletPath;

    public ScopeHttpServletRequestWrapper(
            HttpServletRequest httpServletRequest, String globalCode,
            String localCode) {
        super(httpServletRequest);

        int index = globalCode.length() + localCode.length() + 2;
        String contextPath = httpServletRequest.getContextPath();
        this.requestUri = contextPath
                + httpServletRequest.getRequestURI().substring(
                        index + contextPath.length());
        this.servletPath = httpServletRequest.getServletPath().substring(index);
        this.oldServletPath = httpServletRequest.getServletPath();

        // System.out.println("requestUri : " + requestUri);
        // System.out.println("servletPath : " + servletPath);
    }

    public String getRequestURI() {
        // System.out.println("getRequestURI");
        // System.out.println("old : " + ((HttpServletRequest)getRequest()).getRequestURI());
        // System.out.println("new : " + requestUri);
        return requestUri;
    }

    public String getServletPath() {
        String newServletPath = ((HttpServletRequest) getRequest())
                .getServletPath();

        if (oldServletPath.equals(newServletPath)) {
            return servletPath;
        } else {
            return newServletPath;
        }

        // System.out.println("getServletPath");
        // System.out.println("old : " + ((HttpServletRequest)getRequest()).getServletPath());
        // System.out.println("new : " + servletPath);
    }
}
