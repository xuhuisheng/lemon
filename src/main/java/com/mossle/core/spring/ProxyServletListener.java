package com.mossle.core.spring;

import java.util.Collection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

public class ProxyServletListener implements ServletContextListener,
        HttpSessionListener {
    private static Logger logger = LoggerFactory
            .getLogger(ProxyServletListener.class);
    private ApplicationContext ctx;

    public void contextInitialized(ServletContextEvent sce) {
        ctx = WebApplicationContextUtils.getWebApplicationContext(sce
                .getServletContext());

        if (ctx == null) {
            logger.warn("cannot find applicationContext");

            return;
        }

        Collection<ServletContextListener> servletContextListeners = ctx
                .getBeansOfType(ServletContextListener.class).values();

        for (ServletContextListener servletContextListener : servletContextListeners) {
            servletContextListener.contextInitialized(sce);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        if (ctx == null) {
            logger.warn("cannot find applicationContext");

            return;
        }

        Collection<ServletContextListener> servletContextListeners = ctx
                .getBeansOfType(ServletContextListener.class).values();

        for (ServletContextListener servletContextListener : servletContextListeners) {
            servletContextListener.contextDestroyed(sce);
        }
    }

    public void sessionCreated(HttpSessionEvent se) {
        if (ctx == null) {
            logger.warn("cannot find applicationContext");

            return;
        }

        Collection<HttpSessionListener> httpSessionListeners = ctx
                .getBeansOfType(HttpSessionListener.class).values();

        for (HttpSessionListener httpSessionListener : httpSessionListeners) {
            httpSessionListener.sessionCreated(se);
        }
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        if (ctx == null) {
            logger.warn("cannot find applicationContext");

            return;
        }

        Collection<HttpSessionListener> httpSessionListeners = ctx
                .getBeansOfType(HttpSessionListener.class).values();

        for (HttpSessionListener httpSessionListener : httpSessionListeners) {
            httpSessionListener.sessionDestroyed(se);
        }
    }
}
