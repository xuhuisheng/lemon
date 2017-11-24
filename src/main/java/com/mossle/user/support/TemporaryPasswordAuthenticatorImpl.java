package com.mossle.user.support;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.TemporaryPasswordAuthenticator;

import com.mossle.core.util.RandomCode;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;

public class TemporaryPasswordAuthenticatorImpl implements
        TemporaryPasswordAuthenticator {
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;

    public String authenticate(String userId, String password) {
        AccountInfo accountInfo = accountInfoManager
                .get(Long.parseLong(userId));
        String hql = "from AccountCredential where accountInfo=? and type='temporary'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, accountInfo);

        if (accountCredential == null) {
            return AccountStatus.PASSWORD_NOT_EXISTS;
        }

        if (!accountCredential.getPassword().equals(password)) {
            return AccountStatus.BAD_CREDENTIALS;
        }

        if (accountCredential.getExpireTime().before(new Date())) {
            return AccountStatus.PASSWORD_EXPIRED;
        }

        return AccountStatus.SUCCESS;
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
}
