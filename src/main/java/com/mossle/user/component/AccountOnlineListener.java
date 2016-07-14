package com.mossle.user.component;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.core.auth.LoginEvent;
import com.mossle.core.auth.LogoutEvent;

import com.mossle.user.persistence.domain.AccountOnline;
import com.mossle.user.persistence.manager.AccountOnlineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

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
                accountOnline.setTenantId(loginEvent.getTenantId());
                accountOnlineManager.save(accountOnline);
            }

            if (event instanceof LogoutEvent) {
                LogoutEvent logoutEvent = (LogoutEvent) event;
                logger.debug("logout : {}", logoutEvent);

                AccountOnline accountOnline = accountOnlineManager
                        .findUniqueBy("sessionId", logoutEvent.getSessionId());

                if (accountOnline == null) {
                    accountOnline = accountOnlineManager.findUniqueBy(
                            "account", logoutEvent.getUserId());
                }

                logger.debug("sessionId : {}", logoutEvent.getSessionId());
                logger.debug("accountOnline : {}", accountOnline);

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
