package com.mossle.cdn;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class FilterServlet implements Servlet {
    private ServletConfig servletConfig;
    private Filter filter;

    public void init(ServletConfig config) throws ServletException {
        this.servletConfig = config;
        this.filter.init(null);
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {
        this.filter.doFilter(req, res, null);
    }

    public String getServletInfo() {
        return filter.getClass().getName();
    }

    public void destroy() {
        this.filter.destroy();
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
