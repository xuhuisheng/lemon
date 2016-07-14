package com.mossle.user.component;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.auth.LoginEvent;

import com.mossle.user.persistence.domain.AccountLockInfo;
import com.mossle.user.persistence.domain.AccountLockLog;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.AccountLockInfoManager;
import com.mossle.user.persistence.manager.AccountLockLogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationListener;

// @Component
public class AccountLockListener implements ApplicationListener<LoginEvent> {
    private static Logger logger = LoggerFactory
            .getLogger(AccountLockListener.class);
    public static final int DEFAULT_THREHOLD = 5;
    private AccountInfoManager accountInfoManager;
    private AccountLockInfoManager accountLockInfoManager;
    private AccountLockLogManager accountLockLogManager;
    private int threhold = 5;

    public void onApplicationEvent(LoginEvent event) {
        if (!(event instanceof LoginEvent)) {
            return;
        }

        LoginEvent loginEvent = (LoginEvent) event;
        logger.debug("login : {}", loginEvent);

        try {
            String username = loginEvent.getUserId();
            username = username.toLowerCase();

            if ("success".equals(loginEvent.getResult())) {
                String logHql = "from AccountLockLog where type=? and username=?";
                List<AccountLockLog> accountLockLogs = accountLockLogManager
                        .find(logHql, loginEvent.getType(), username);
                accountLockLogManager.removeAll(accountLockLogs);

                String infoHql = "from AccountLockInfo where type=? and username=?";
                List<AccountLockInfo> accountLockInfos = accountLockInfoManager
                        .find(infoHql, loginEvent.getType(), username);
                accountLockInfoManager.removeAll(accountLockInfos);
            } else if ("badCredentials".equals(loginEvent.getResult())) {
                AccountLockLog accountLockLog = new AccountLockLog();
                accountLockLog.setType(loginEvent.getType());
                accountLockLog.setLockTime(new Date());
                accountLockLog.setUsername(username);
                accountLockLogManager.save(accountLockLog);

                String logHql = "from AccountLockLog where type=? and username=?";
                List<AccountLockLog> accountLockLogs = accountLockLogManager
                        .find(logHql, loginEvent.getType(), username);

                if (accountLockLogs.size() > threhold) {
                    AccountLockInfo accountLockInfo = new AccountLockInfo();
                    accountLockInfo.setType(loginEvent.getType());
                    accountLockInfo.setLockTime(new Date());
                    accountLockInfo.setUsername(username);
                    accountLockInfoManager.save(accountLockInfo);
                }
            } else {
                logger.info("other loginEvent : {}", loginEvent.getResult());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAccountLockInfoManager(
            AccountLockInfoManager accountLockInfoManager) {
        this.accountLockInfoManager = accountLockInfoManager;
    }

    @Resource
    public void setAccountLockLogManager(
            AccountLockLogManager accountLockLogManager) {
        this.accountLockLogManager = accountLockLogManager;
    }

    public void setThrehold(int threhold) {
        this.threhold = threhold;
    }
}
