package com.mossle.security.util;

import java.io.IOException;

import javax.annotation.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.security.SecurityConstants;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class RememberLastUsernameAuthenticationFailureHandler extends
        SimpleUrlAuthenticationFailureHandler {
    private TenantHolder tenantHolder;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        session.setAttribute(
                SecurityConstants.SECURITY_LAST_USERNAME,
                request.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY));
        session.setAttribute(SecurityConstants.SECURITY_LAST_TENANT,
                tenantHolder.getTenantCode());

        Integer failureCount = (Integer) session
                .getAttribute("SECURITY_FAILURE_COUNT");

        if (failureCount == null) {
            failureCount = 0;
        }

        failureCount++;

        if (failureCount > 3) {
            session.removeAttribute("SECURITY_FAILURE_COUNT");
            session.setAttribute("captchaSessionToken", true);
        } else {
            session.setAttribute("SECURITY_FAILURE_COUNT", failureCount);
        }

        super.onAuthenticationFailure(request, response, exception);
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
