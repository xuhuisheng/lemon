package com.mossle.user.authenticate;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.AccountStatusHelper;
import com.mossle.api.user.AuthenticationClient;
import com.mossle.api.user.AuthenticationHandler;
import com.mossle.api.user.AuthenticationType;

import com.mossle.core.auth.CustomPasswordEncoder;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecialOrNormalAuthenticationHandler implements
        AuthenticationHandler {
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private AuthenticationHandler specialAuthenticationHandler;
    private AuthenticationHandler normalAuthenticationHandler;

    public boolean support(String type) {
        return AuthenticationType.SPECIAL_OR_NORMAL.equals(type);
    }

    public String doAuthenticate(String username, String password,
            String application) {
        String result = specialAuthenticationHandler.doAuthenticate(username,
                password, application);

        if (AccountStatus.PASSWORD_NOT_EXISTS.equals(result)) {
            return normalAuthenticationHandler.doAuthenticate(username,
                    password, application);
        }

        return result;
    }

    @Resource
    public void setSpecialAuthenticationHandler(
            AuthenticationHandler specialAuthenticationHandler) {
        this.specialAuthenticationHandler = specialAuthenticationHandler;
    }

    @Resource
    public void setNormalAuthenticationHandler(
            AuthenticationHandler normalAuthenticationHandler) {
        this.normalAuthenticationHandler = normalAuthenticationHandler;
    }
}
