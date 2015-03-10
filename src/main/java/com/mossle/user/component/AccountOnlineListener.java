package com.mossle.user.component;

import java.net.InetAddress;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.audit.AuditConnector;
import com.mossle.api.audit.AuditDTO;

import com.mossle.ext.auth.LoginEvent;
import com.mossle.ext.auth.LogoutEvent;

import com.mossle.user.persistence.domain.AccountOnline;
import com.mossle.user.persistence.manager.AccountOnlineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import org.springframework.security.access.event.AuthenticationCredentialsNotFoundEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.access.event.PublicInvocationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import org.springframework.stereotype.Component;

@Component
public class AccountOnlineListener implements ApplicationListener {
    private static Logger logger = LoggerFactory
            .getLogger(AccountOnlineListener.class);
    private AccountOnlineManager accountOnlineManager;

    public void onApplicationEvent(ApplicationEvent event) {
        try {
            if (event instanceof LoginEvent) {
                LoginEvent loginEvent = (LoginEvent) event;
                logger.debug("login : {}", loginEvent);

                AccountOnline accountOnline = new AccountOnline();
                accountOnline.setAccount(loginEvent.getUserId());
                accountOnline.setSessionId(loginEvent.getSessionId());
                accountOnline.setLoginTime(new Date());
                accountOnlineManager.save(accountOnline);
            }

            if (event instanceof LogoutEvent) {
                LogoutEvent logoutEvent = (LogoutEvent) event;
                logger.debug("logout : {}", logoutEvent);

                AccountOnline accountOnline = accountOnlineManager
                        .findUniqueBy("sessionId", logoutEvent.getSessionId());

                if (accountOnline != null) {
                    accountOnlineManager.remove(accountOnline);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Resource
    public void setAccountOnlineManager(
            AccountOnlineManager accountOnlineManager) {
        this.accountOnlineManager = accountOnlineManager;
    }
}
