package com.mossle.user.web.rs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.UserDTO;

import com.mossle.core.util.BaseDTO;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;

import org.springframework.util.Assert;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/rs/remote")
public class UserRemoteController {
    private static Logger logger = LoggerFactory
            .getLogger(UserRemoteController.class);
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private PersonInfoManager personInfoManager;
    private CustomPasswordEncoder customPasswordEncoder;

    @RequestMapping(value = "findById", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findById(@RequestParam("id") String id,
            @RequestParam("userRepoRef") String userRepoRef) {
        Assert.hasText(id, "user id should not be null");

        String code = id;

        AccountInfo accountInfo = accountInfoManager.findUniqueBy("code", code);
        PersonInfo personInfo = personInfoManager.findUniqueBy("code", code);

        UserDTO userDto = this.convertUserDto(accountInfo, personInfo);
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(userDto);

        return baseDto;
    }

    @RequestMapping(value = "findByUsername", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findByUsername(@RequestParam("username") String username,
            @RequestParam("userRepoRef") String userRepoRef) {
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

        UserDTO userDto = this.convertUserDto(accountInfo, personInfo);
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(userDto);

        return baseDto;
    }

    @RequestMapping(value = "authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO authenticate(@RequestParam("username") String username,
            @RequestParam("password") String password) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);
        String hql = "from AccountCredential where accountInfo=? and catalog='default'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, accountInfo);

        if (accountCredential == null) {
            logger.info("cannot find credential : {} {}", username, "xxx");

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(AccountStatus.FAILURE);

            return baseDto;
        }

        String encodedPassword = accountCredential.getPassword();

        boolean authenticated = customPasswordEncoder.matches(password,
                encodedPassword);
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(authenticated ? AccountStatus.SUCCESS
                : AccountStatus.FAILURE);

        return baseDto;
    }

    @RequestMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO search(@RequestParam("query") String query) {
        if (StringUtils.isBlank(query)) {
            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(Collections.emptyList());

            return baseDto;
        }

        String hql = "from AccountInfo where username like ?";
        List<AccountInfo> accountInfos = accountInfoManager.find(hql, "%"
                + query + "%");
        List<UserDTO> userDtos = new ArrayList<UserDTO>();

        for (AccountInfo accountInfo : accountInfos) {
            String code = accountInfo.getCode();
            PersonInfo personInfo = personInfoManager
                    .findUniqueBy("code", code);
            UserDTO userDto = this.convertUserDto(accountInfo, personInfo);
            userDtos.add(userDto);
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(userDtos);

        return baseDto;
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

    // ~ ======================================================================
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
