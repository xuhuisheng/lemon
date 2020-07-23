package com.mossle.user.support;

import java.util.ArrayList;
import java.util.Collections;
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

import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.query.PropertyFilterUtils;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class RemoteUserConnectorImpl implements RemoteUserConnector {
    private static Logger logger = LoggerFactory
            .getLogger(RemoteUserConnectorImpl.class);
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private PersonInfoManager personInfoManager;
    private CustomPasswordEncoder customPasswordEncoder;

    public UserDTO findById(String id, String userRepoRef) {
        Assert.hasText(id, "user id should not be null");

        String code = id;

        AccountInfo accountInfo = accountInfoManager.findUniqueBy("code", code);
        PersonInfo personInfo = personInfoManager.findUniqueBy("code", code);

        return this.convertUserDto(accountInfo, personInfo);
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        Assert.hasText(username, "username should not be null");

        username = username.trim().toLowerCase();

        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);

        if (accountInfo == null) {
            logger.info("cannot find user : {}", username);

            return null;
        }

        String code = accountInfo.getCode();
        PersonInfo personInfo = personInfoManager.findUniqueBy("code", code);

        return this.convertUserDto(accountInfo, personInfo);
    }

    public boolean authenticate(String username, String password) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);
        String hql = "from AccountCredential where accountInfo=? and catalog='default'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, accountInfo);
        String encodedPassword = accountCredential.getPassword();

        return customPasswordEncoder.matches(password, encodedPassword);
    }

    protected UserDTO convertUserDto(AccountInfo accountInfo,
            PersonInfo personInfo) {
        if (accountInfo == null) {
            logger.info("accountInfo cannot be null");

            return null;
        }

        UserDTO userDto = new UserDTO();
        userDto.setId(accountInfo.getCode());
        userDto.setUsername(accountInfo.getUsername());
        userDto.setNickName(accountInfo.getNickName());
        userDto.setDisplayName(accountInfo.getDisplayName());
        userDto.setStatus("active".equals(accountInfo.getStatus()) ? 1 : 0);
        userDto.setUserRepoRef(accountInfo.getTenantId());

        if (personInfo == null) {
            logger.info("personInfo cannot be null");

            return userDto;
        }

        userDto.setEmail(personInfo.getEmail());
        userDto.setMobile(personInfo.getCellphone());

        return userDto;
    }

    public List<UserDTO> search(String query) {
        if (StringUtils.isBlank(query)) {
            return Collections.emptyList();
        }

        String hql = "from AccountInfo where username like ?";
        Page page = accountInfoManager
                .pagedQuery(hql, 1, 10, "%" + query + "%");
        List<AccountInfo> accountInfos = (List<AccountInfo>) page.getResult();
        List<UserDTO> userDtos = new ArrayList<UserDTO>();

        for (AccountInfo accountInfo : accountInfos) {
            String code = accountInfo.getCode();
            PersonInfo personInfo = personInfoManager
                    .findUniqueBy("code", code);
            UserDTO userDto = this.convertUserDto(accountInfo, personInfo);
            userDtos.add(userDto);
        }

        return userDtos;
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
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }

    @Resource
    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }
}
