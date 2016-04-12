package com.mossle.security.util;

import java.io.IOException;

import javax.annotation.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.LogoutEvent;

import com.mossle.security.impl.SpringSecurityUserAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

/**
 * 主要为了把logoutEvent发布出去.
 */
public class LogoutSuccessHandlerImpl extends SimpleUrlLogoutSuccessHandler
        implements ApplicationContextAware {
    private static Logger logger = LoggerFactory
            .getLogger(LogoutSuccessHandlerImpl.class);
    private TenantHolder tenantHolder;
    private ApplicationContext ctx;

    // setDefaultTargetUrl
    public void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        super.handle(request, response, authentication);

        if (authentication == null) {
            logger.info("authentication is null");

            return;
        }

        String tenantId = tenantHolder.getTenantId();

        String userId = this.getUserId(authentication);
        String sessionId = this.getSessionId(authentication);
        LogoutEvent logoutEvent = new LogoutEvent(authentication, userId,
                sessionId, tenantId);
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

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }
}
