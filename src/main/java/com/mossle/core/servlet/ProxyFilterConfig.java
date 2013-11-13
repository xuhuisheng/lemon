package com.mossle.core.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class ProxyFilterConfig implements FilterConfig {
    private String filterName;
    private ServletContext servletContext;
    private Map<String, String> map = Collections.EMPTY_MAP;

    public ProxyFilterConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getFilterName() {
        return filterName;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getInitParameter(String name) {
        return map.get(name);
    }

    public Enumeration getInitParameterNames() {
        return Collections.enumeration(map.keySet());
    }

    // ~ ==================================================
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
