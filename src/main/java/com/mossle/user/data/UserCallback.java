package com.mossle.user.data;

import java.util.Date;
import java.util.List;

import com.mossle.api.auth.CustomPasswordEncoder;

import com.mossle.core.csv.CsvCallback;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCallback implements CsvCallback {
    private static Logger logger = LoggerFactory.getLogger(UserCallback.class);
    public static final int PREFIX = 80000;
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private PersonInfoManager personInfoManager;
    private CustomPasswordEncoder customPasswordEncoder;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String username = list.get(0);
        String displayName = list.get(1);
        String cellphone = list.get(2);
        String email = list.get(3);

        if (StringUtils.isBlank(username)) {
            logger.warn("username cannot be blank {} {}", lineNo, list);

            return;
        }

        username = username.toLowerCase();

        this.createOrUpdateAccountInfo(username, displayName, lineNo);
        this.createOrUpdateAccountCredential(username);
        this.createOrUpdatePersonInfo(username, cellphone, email);
    }

    public void createOrUpdateAccountInfo(String username, String displayName,
            int lineNo) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);

        if (accountInfo == null) {
            // insert
            accountInfo = new AccountInfo();
            accountInfo.setCode(Integer.toString((PREFIX + lineNo) - 1));
            accountInfo.setUsername(username);
            accountInfo.setType("employee");
            accountInfo.setDisplayName(displayName);
            accountInfo.setStatus("active");
            accountInfo.setPasswordRequired("require");
            accountInfo.setCreateTime(new Date());
            accountInfo.setNickName(displayName);
            accountInfo.setTenantId(defaultTenantId);
            accountInfoManager.save(accountInfo);

            return;
        }

        if (!displayName.equals(accountInfo.getDisplayName())) {
            logger.info("{} update {} to {}", username,
                    accountInfo.getDisplayName(), displayName);
            accountInfo.setDisplayName(displayName);
            accountInfoManager.save(accountInfo);

            return;
        }
    }

    public void createOrUpdateAccountCredential(String username) {
        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy(
                "username", username);

        if (accountInfo == null) {
            logger.info("cannot find accountInfo : {}", username);

            return;
        }

        String hql = "from AccountCredential where accountInfo=? and catalog=?";
        AccountCredential accountCredential = this.accountCredentialManager
                .findUnique(hql, accountInfo, "default");

        if (accountCredential != null) {
            logger.info("credential exists : {}", username);

            return;
        }

        accountCredential = new AccountCredential();
        accountCredential.setCatalog("default");
        accountCredential.setType("normal");
        accountCredential.setModifyTime(new Date());
        accountCredential.setAccountInfo(accountInfo);
        accountCredential.setExpireTime(null);
        accountCredential.setExpireStatus("never");
        accountCredential.setRequired("true");
        accountCredential.setCouldModify("true");
        accountCredential.setStatus("active");
        accountCredential.setTenantId(defaultTenantId);
        accountCredential.setPassword(customPasswordEncoder.encode("1"));
        this.accountCredentialManager.save(accountCredential);
    }

    public void createOrUpdatePersonInfo(String username, String cellphone,
            String email) {
        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy(
                "username", username);

        if (accountInfo == null) {
            logger.info("cannot find accountInfo : {}", username);

            return;
        }

        PersonInfo personInfo = this.personInfoManager.findUniqueBy("username",
                username);

        if (personInfo == null) {
            personInfo = new PersonInfo();
            personInfo.setCode(accountInfo.getCode());
            personInfo.setUsername(username);
            personInfo.setCellphone(cellphone);
            personInfo.setEmail(email);
            personInfo.setTenantId(defaultTenantId);
            personInfoManager.save(personInfo);

            return;
        }

        if (!accountInfo.getCode().equals(personInfo.getCode())) {
            logger.info("{} update {} to {}", username, personInfo.getCode(),
                    accountInfo.getCode());
            personInfo.setCode(accountInfo.getCode());
            personInfoManager.save(personInfo);
        }

        if (!cellphone.equals(personInfo.getCellphone())) {
            logger.info("{} update {} to {}", username,
                    personInfo.getCellphone(), cellphone);
            personInfo.setCellphone(cellphone);
            personInfoManager.save(personInfo);
        }

        if (!email.equals(personInfo.getEmail())) {
            logger.info("{} update {} to {}", username, personInfo.getEmail(),
                    email);
            personInfo.setEmail(email);
            personInfoManager.save(personInfo);
        }
    }

    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    public void setAccountCredentialManager(
            AccountCredentialManager accountCredentialManager) {
        this.accountCredentialManager = accountCredentialManager;
    }

    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }

    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }
}
