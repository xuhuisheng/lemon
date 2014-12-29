package com.mossle.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.ext.auth.LogoutEvent;

import com.mossle.security.impl.SpringSecurityUserAuth;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class LogoutSuccessHandlerImpl extends SimpleUrlLogoutSuccessHandler
        implements ApplicationContextAware {
    private ApplicationContext ctx;

    // setDefaultTargetUrl
    public void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        super.handle(request, response, authentication);

        String userId = this.getUserId(authentication);
        String sessionId = this.getSessionId(authentication);
        LogoutEvent logoutEvent = new LogoutEvent(authentication, userId,
                sessionId);
        ctx.publishEvent(logoutEvent);
    }

    public String getUserId(Authentication authentication) {
        if (authentication == null) {
            return "";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof SpringSecurityUserAuth) {
            return ((SpringSecurityUserAuth) principal).getId();
        } else {
            return authentication.getName();
        }
    }

    public String getSessionId(Authentication authentication) {
        if (authentication == null) {
            return "";
        }

        Object details = authentication.getDetails();

        if (!(details instanceof WebAuthenticationDetails)) {
            return "";
        }

        WebAuthenticationDetails webDetails = (WebAuthenticationDetails) details;

        return webDetails.getSessionId();
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }
}
