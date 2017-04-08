package com.mossle.user.support;

import java.util.Calendar;

import javax.annotation.Resource;

import com.mossle.api.user.TemporaryPasswordGenerator;

import com.mossle.core.util.RandomCode;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;

public class TemporaryPasswordGeneratorImpl implements
        TemporaryPasswordGenerator {
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;

    public String generate(String userId, int minute) {
        String password = Integer.toString(RandomCode.generate());
        AccountInfo accountInfo = accountInfoManager
                .get(Long.parseLong(userId));
        String hql = "from AccountCredential where accountInfo=? and type='temporary'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, accountInfo);

        if (accountCredential == null) {
            accountCredential = new AccountCredential();
            accountCredential.setAccountInfo(accountInfo);
        }

        accountCredential.setPassword(password);

        Calendar calendar = Calendar.getInstance();
        calendar.add(minute, Calendar.MINUTE);
        accountCredential.setExpireTime(calendar.getTime());
        accountCredentialManager.save(accountCredential);

        return password;
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
