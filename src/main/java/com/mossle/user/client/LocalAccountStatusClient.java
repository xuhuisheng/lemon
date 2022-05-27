package com.mossle.user.client;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.client.user.AccountStatusClient;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.AccountLockInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.AccountLockInfoManager;

public class LocalAccountStatusClient implements AccountStatusClient {
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private AccountLockInfoManager accountLockInfoManager;

    public boolean findEnabled(String userId) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("code",
                userId);

        if (accountInfo == null) {
            return false;
        }

        return "active".equals(accountInfo.getStatus());
    }

    public boolean findCredentialsExpired(String userId) {
        String hql = "from AccountCredential where accountInfo.code=? and catalog='default'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, userId);

        if (accountCredential == null) {
            // cannot find password, not expire
            return false;
        }

        Date expireTime = accountCredential.getExpireTime();

        if (expireTime == null) {
            // no expireTime, not expire
            return false;
        }

        return expireTime.before(new Date());
    }

    public boolean findAccountLocked(String userId) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("code",
                userId);

        if (accountInfo == null) {
            // cannot find account, not lock
            return false;
        }

        String hql = "from AccountLockInfo where username=? and type='default'";

        // TODO: use userId
        AccountLockInfo accountLockInfo = accountLockInfoManager.findUnique(
                hql, accountInfo.getUsername());

        return accountLockInfo != null;
    }

    public boolean findAccountExpired(String userId) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("code",
                userId);

        if (accountInfo == null) {
            // cannot find account, not expire
            return false;
        }

        Date expireTime = accountInfo.getCloseTime();

        if (expireTime == null) {
            // no expireTime, not expire
            return false;
        }

        return expireTime.before(new Date());
    }

    // ~
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
