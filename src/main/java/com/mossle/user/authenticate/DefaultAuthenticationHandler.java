package com.mossle.user.authenticate;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.AuthenticationHandler;
import com.mossle.api.user.AuthenticationType;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAuthenticationHandler {
    private static Logger logger = LoggerFactory
            .getLogger(DefaultAuthenticationHandler.class);
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private CustomPasswordEncoder customPasswordEncoder;
    private SimpleStrategy strategy = new SimpleStrategy();
    private boolean accountNotFoundAsCredentialsNotMatch = true;

    // cache
    private AuthenticationCache authenticationCache = new AuthenticationCache();
    private CaptchaCache captchaCache = new CaptchaCache();
    private SessionCache sessionCache = new SessionCache();
    private boolean concurrentLogin = false;

    /**
     * 认证入口.
     */
    public void authenticate(AuthenticationParam param) {
        // 校验参数
        this.strategy.validateParam(param);

        // 规范帐号
        String username = this.strategy.normalizeUsername(param);

        // 2fa
        this.validate2fa(username, param);

        // 获取帐号信息
        AuthenticationAccount account = null;

        try {
            account = this.findAuthenticationAccount(username);
        } catch (AccountNotFoundException ex) {
            if (accountNotFoundAsCredentialsNotMatch) {
                this.postCheckCredentialsFailure(username);

                throw new CredentialsNotMatchException();
            } else {
                throw ex;
            }
        }

        // 校验密码
        boolean credentialsMatched = this.checkCredentials(account,
                param.getPassword());

        if (credentialsMatched) {
            this.postCheckCredentialsSuccess(username);
        } else {
            this.postCheckCredentialsFailure(username);
            throw new CredentialsNotMatchException();
        }
    }

    public void validate2fa(String username, AuthenticationParam param) {
        int count = authenticationCache.getCredentialsInvalidCount(username);

        if (count > 6) {
            // 10分钟6次密码错误
            // TODO: ExcessiveAttemptsException
            throw new AccountLockedException();
        } else if (count > 3) {
            // TODO: 10分钟3次密码错误，校验captcha
            String captcha = param.getParam2faValue();

            if (StringUtils.isBlank(captcha)) {
                // TODO: 参数非空校验
                throw new RuntimeException("captcha required");
            }

            this.captchaCache.checkCaptcha(username, captcha);
        }
    }

    public AuthenticationAccount findAuthenticationAccount(String username) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);

        if (accountInfo == null) {
            throw new AccountNotFoundException();
        }

        if ("1".equals(accountInfo.getLocked())) {
            throw new AccountLockedException();
        }

        if ("disabled".equals(accountInfo.getStatus())) {
            throw new AccountDisabledException();
        }

        Date now = new Date();

        if ((accountInfo.getCloseTime() != null)
                && now.after(accountInfo.getCloseTime())) {
            throw new AccountExpiredException();
        }

        String userId = accountInfo.getCode();

        AuthenticationAccount authenticationAccount = new AuthenticationAccount();
        authenticationAccount.setUserId(userId);
        authenticationAccount.setUsername(username);

        return authenticationAccount;
    }

    public boolean checkCredentials(AuthenticationAccount account,
            String credential) {
        String hql = "from AccountCredential where accountInfo.code=? and catalog='default'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, account.getUserId());

        if (accountCredential == null) {
            throw new CredentialsNotFoundException();
        }

        Date now = new Date();

        if ((accountCredential.getExpireTime() != null)
                && now.after(accountCredential.getExpireTime())) {
            throw new CredentialsExpiredException();
        }

        // TODO: credentials must reset
        return customPasswordEncoder.matches(credential,
                accountCredential.getPassword());
    }

    public void postCheckCredentialsSuccess(String username) {
        // 清除密码错误次数
        this.authenticationCache.clearCredentialsInvalidCount(username);

        // TODO: change session id

        // TODO: session
        if (concurrentLogin) {
            sessionCache.kickSessionsByUsername(username);
            sessionCache.updateSession(username, "sessionId");
        }
    }

    public void postCheckCredentialsFailure(String username) {
        // 记录密码错误次数
        authenticationCache.addCredentialsInvalidCount(username);
    }

    public void setCaptcha(String username, String captcha) {
        captchaCache.setCaptcha(username, captcha);
    }

    //
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
