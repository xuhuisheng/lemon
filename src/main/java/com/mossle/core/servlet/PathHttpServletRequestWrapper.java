package com.mossle.core.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class PathHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private String servletPath;
    private HttpServletRequest httpServletRequest;

    public PathHttpServletRequestWrapper(HttpServletRequest request, String path) {
        super(request);
        this.httpServletRequest = request;

        if (path.startsWith("/")) {
            if (path.endsWith("*")) {
                servletPath = path.substring(0, path.length() - 1);
            } else {
                servletPath = path;
            }
        } else {
            servletPath = "";
        }

        if (servletPath.endsWith("/")) {
            servletPath = servletPath.substring(0, servletPath.length() - 1);
        }
    }

    public String getServletPath() {
        return servletPath;
    }

    public String getPathInfo() {
        return httpServletRequest.getRequestURI().substring(
                httpServletRequest.getContextPath().length()
                        + servletPath.length());
    }
}
