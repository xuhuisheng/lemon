package com.mossle.user.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.user.LocalUserConnector;
import com.mossle.api.user.RemoteUserConnector;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;
import com.mossle.api.user.UserSyncConnector;

import com.mossle.client.authn.AuthnClient;

import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.query.PropertyFilterUtils;
import com.mossle.core.util.BaseDTO;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class AuthnClientImpl implements AuthnClient {
    private static Logger logger = LoggerFactory
            .getLogger(AuthnClientImpl.class);
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private CustomPasswordEncoder customPasswordEncoder;

    public BaseDTO authenticate(String username, String password,
            String tenantId) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);
        String hql = "from AccountCredential where accountInfo=? and catalog='default'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, accountInfo);
        String encodedPassword = accountCredential.getPassword();

        boolean result = customPasswordEncoder.matches(password,
                encodedPassword);
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(username);

        if (!result) {
            baseDto.setCode(400);
            baseDto.setMessage("failure");
        }

        return baseDto;
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
