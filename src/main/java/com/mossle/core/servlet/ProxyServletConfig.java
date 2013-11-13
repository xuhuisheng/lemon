package com.mossle.core.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ProxyServletConfig implements ServletConfig {
    private String servletName;
    private ServletContext servletContext;
    private Map<String, String> map = Collections.EMPTY_MAP;

    public ProxyServletConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getServletName() {
        return servletName;
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
    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
