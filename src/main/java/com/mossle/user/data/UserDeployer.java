package com.mossle.user.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDeployer {
    private static Logger logger = LoggerFactory.getLogger(UserDeployer.class);
    public static final int PREFIX = 80000;
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private PersonInfoManager personInfoManager;
    private CustomPasswordEncoder customPasswordEncoder;
    private String dataFilePath = "data/user.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init user data");

            return;
        }

        UserCallback userCallback = new UserCallback();
        userCallback.setAccountInfoManager(accountInfoManager);
        userCallback.setAccountCredentialManager(accountCredentialManager);
        userCallback.setPersonInfoManager(personInfoManager);
        userCallback.setCustomPasswordEncoder(customPasswordEncoder);
        new CsvProcessor()
                .process(dataFilePath, dataFileEncoding, userCallback);
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
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }

    @Resource
    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }
}
