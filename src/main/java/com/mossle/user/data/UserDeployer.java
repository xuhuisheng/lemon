package com.mossle.user.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountDeptManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDeployer {
    private static Logger logger = LoggerFactory.getLogger(UserDeployer.class);
    public static final int PREFIX = 80000;
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private AccountDeptManager accountDeptManager;
    private PersonInfoManager personInfoManager;
    private CustomPasswordEncoder customPasswordEncoder;
    private String dataFilePath = "data/user/user.csv";
    private String dataFileEncoding = "GB2312";
    private String userDeptDataFilePath = "data/user/dept.json";
    private String userDeptDataEncoding = "UTF-8";
    private String userPersonDataFilePath = "data/user/person.csv";
    private String userPersonDataEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;
    private UserDeptProcessor userDeptProcessor = new UserDeptProcessor();

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init user data");

            return;
        }

        // user
        UserCallback userCallback = new UserCallback();
        userCallback.setAccountInfoManager(accountInfoManager);
        userCallback.setAccountCredentialManager(accountCredentialManager);
        userCallback.setPersonInfoManager(personInfoManager);
        userCallback.setCustomPasswordEncoder(customPasswordEncoder);
        userCallback.setDefaultTenantId(defaultTenantId);
        new CsvProcessor()
                .process(dataFilePath, dataFileEncoding, userCallback);
        // dept
        this.userDeptProcessor.init(userDeptDataFilePath, userDeptDataEncoding);
        this.userDeptProcessor.setAccountDeptManager(accountDeptManager);
        this.userDeptProcessor.process();

        // person
        UserPersonCallback userPersonCallback = new UserPersonCallback();
        userPersonCallback.setPersonInfoManager(personInfoManager);
        userPersonCallback.setAccountDeptManager(accountDeptManager);
        new CsvProcessor().process(userPersonDataFilePath,
                userPersonDataEncoding, userPersonCallback);
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

    @Resource
    public void setAccountDeptManager(AccountDeptManager accountDeptManager) {
        this.accountDeptManager = accountDeptManager;
    }
}
