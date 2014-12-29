package com.mossle.ext.spring;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.mossle.ext.auth.LogoutEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

public class LogoutHttpSessionListener implements HttpSessionListener {
    private static Logger logger = LoggerFactory
            .getLogger(LogoutHttpSessionListener.class);

    public void sessionCreated(HttpSessionEvent se) {
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        ApplicationContext ctx = WebApplicationContextUtils
                .getWebApplicationContext(se.getSession().getServletContext());

        if (ctx == null) {
            logger.warn("cannot find applicationContext");

            return;
        }

        HttpSession session = se.getSession();
        LogoutEvent logoutEvent = new LogoutEvent(session, null,
                session.getId());
        ctx.publishEvent(logoutEvent);
    }
}
