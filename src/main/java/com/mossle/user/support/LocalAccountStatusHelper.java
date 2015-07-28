package com.mossle.user.support;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.AccountStatusHelper;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.AccountLockInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.AccountLockInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalAccountStatusHelper implements AccountStatusHelper {
    private static Logger logger = LoggerFactory
            .getLogger(LocalAccountStatusHelper.class);
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private AccountLockInfoManager accountLockInfoManager;

    public boolean isLocked(String username, String application) {
        AccountLockInfo accountLockInfo = accountLockInfoManager.findUnique(
                "from AccountLockInfo where username=? and type=?", username,
                application);

        return accountLockInfo != null;
    }

    public String getAccountStatus(String username, String application) {
        AccountInfo accountInfo = accountInfoManager.findUnique(
                "from AccountInfo where username=?", username);

        if (!"1".equals(accountInfo.getStatus())) {
            return AccountStatus.ACCOUNT_DISABLED;
        }

        Date now = new Date();

        try {
            // account expire
            Date accountExpireDate = accountInfo.getCloseTime();

            if ((accountExpireDate != null) && accountExpireDate.before(now)) {
                return AccountStatus.ACCOUNT_EXPIRED;
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }

        // TODO: password must change
        try {
            AccountCredential accountCredential = accountCredentialManager
                    .findUnique("from AccountCredential from accountInfo=? and catalog='normal'");

            // password expire
            Date passwordExpireDate = accountCredential.getExpireTime();

            if ((passwordExpireDate != null) && passwordExpireDate.before(now)) {
                return AccountStatus.PASSWORD_EXPIRED;
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }

        return AccountStatus.ENABLED;
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAccountCredentialManager(
            AccountCredentialManager accountCredentialManager) {
        this.accountCredentialManager = accountCredentialManager;
    }

    @Resource
    public void setAccountLockInfoManager(
            AccountLockInfoManager accountLockInfoManager) {
        this.accountLockInfoManager = accountLockInfoManager;
    }
}
