package com.mossle.user.authenticate;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.AccountStatusHelper;
import com.mossle.api.user.AuthenticationClient;
import com.mossle.api.user.AuthenticationHandler;
import com.mossle.api.user.AuthenticationType;

import com.mossle.ext.auth.CustomPasswordEncoder;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NormalAuthenticationHandler implements AuthenticationHandler {
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private CustomPasswordEncoder customPasswordEncoder;

    public boolean support(String type) {
        return AuthenticationType.NORMAL.equals(type);
    }

    public String doAuthenticate(String username, String password,
            String application) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);

        if (accountInfo == null) {
            return AccountStatus.ACCOUNT_NOT_EXISTS;
        }

        String hql = "from AccountCredential from accountInfo=? and catalog='normal'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, accountInfo);

        if (accountCredential == null) {
            return AccountStatus.PASSWORD_NOT_EXISTS;
        }

        if (customPasswordEncoder.matches(password,
                accountCredential.getPassword())) {
            return AccountStatus.SUCCESS;
        } else {
            return AccountStatus.BAD_CREDENTIALS;
        }
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
    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }
}
