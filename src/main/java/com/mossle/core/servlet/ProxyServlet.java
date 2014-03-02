package com.mossle.core.servlet;

import java.io.IOException;

import java.util.Collections;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyServlet implements Servlet {
    private static Logger logger = LoggerFactory.getLogger(ProxyServlet.class);
    private final String name;
    private final Servlet servlet;
    private final Map<String, String> map;
    private final boolean enable;

    public ProxyServlet(String name, Servlet servlet) {
        this(name, servlet, Collections.EMPTY_MAP, true);
    }

    public ProxyServlet(String name, Servlet servlet, Map<String, String> map) {
        this(name, servlet, map, true);
    }

    public ProxyServlet(String name, Servlet servlet, boolean enable) {
        this(name, servlet, Collections.EMPTY_MAP, enable);
    }

    public ProxyServlet(String name, Servlet servlet, Map<String, String> map,
            boolean enable) {
        this.name = name;
        this.servlet = servlet;
        this.map = map;
        this.enable = enable;
    }

    public void init(ServletConfig config) throws ServletException {
        if (enable) {
            ProxyServletConfig proxyServletConfig = new ProxyServletConfig(
                    config.getServletContext());
            proxyServletConfig.setServletName(name);
            proxyServletConfig.setMap(map);
            servlet.init(proxyServletConfig);
        }
    }

    public ServletConfig getServletConfig() {
        return servlet.getServletConfig();
    }

    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {
        logger.trace("{}", name);

        if (enable) {
            servlet.service(req, res);
        } else {
            logger.trace("skip");
            ((HttpServletResponse) res)
                    .sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public String getServletInfo() {
        return name;
    }

    public void destroy() {
        if (enable) {
            servlet.destroy();
        }
    }

    // ~ ==================================================
    public String getName() {
        return name;
    }
}
