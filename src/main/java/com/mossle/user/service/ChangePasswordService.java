package com.mossle.user.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.auth.CustomPasswordEncoder;
import com.mossle.core.util.StringUtils;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.policy.PasswordPolicy;
import com.mossle.user.support.ChangePasswordResult;

import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private CustomPasswordEncoder customPasswordEncoder;

    public ChangePasswordResult changePassword(String username,
            String oldPassword, String newPassword, String confirmPassword) {
        ChangePasswordResult changePasswordResult = new ChangePasswordResult();

        if (StringUtils.isBlank(username)) {
            changePasswordResult.setCode("user.user.input.username.blank");
            changePasswordResult.setMessage("账号不能为空");

            return changePasswordResult;
        }

        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);

        return this.changePassword(accountInfo, oldPassword, newPassword,
                confirmPassword);
    }

    public ChangePasswordResult changePassword(Long accountId,
            String oldPassword, String newPassword, String confirmPassword) {
        ChangePasswordResult changePasswordResult = new ChangePasswordResult();

        if (accountId == null) {
            changePasswordResult.setCode("user.user.input.accountid.blank");
            changePasswordResult.setMessage("账号不能为空");

            return changePasswordResult;
        }

        AccountInfo accountInfo = accountInfoManager.get(accountId);

        return this.changePassword(accountInfo, oldPassword, newPassword,
                confirmPassword);
    }

    public ChangePasswordResult changePassword(AccountInfo accountInfo,
            String oldPassword, String newPassword, String confirmPassword) {
        ChangePasswordResult changePasswordResult = new ChangePasswordResult();

        if (accountInfo == null) {
            changePasswordResult.setCode("user.user.input.account.notexist");
            changePasswordResult.setMessage("找不到账号");

            return changePasswordResult;
        }

        if (StringUtils.isBlank(oldPassword)) {
            changePasswordResult.setCode("user.user.input.oldPassword.blank");
            changePasswordResult.setMessage("请输入旧密码");

            return changePasswordResult;
        }

        if (StringUtils.isBlank(newPassword)) {
            changePasswordResult.setCode("user.user.input.newPassword.blank");
            changePasswordResult.setMessage("新密码不能为空");

            return changePasswordResult;
        }

        if (!newPassword.equals(confirmPassword)) {
            changePasswordResult.setCode("user.user.input.passwordnotequals");
            changePasswordResult.setMessage("两次输入密码不符");

            return changePasswordResult;
        }

        String hql = "from AccountCredential where accountInfo=? and catalog='default'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, accountInfo);

        if (accountCredential == null) {
            changePasswordResult.setCode("user.user.input.credentialnotexists");
            changePasswordResult.setMessage("未设置过密码");

            return changePasswordResult;
        }

        if (!isPasswordValid(oldPassword, accountCredential.getPassword())) {
            changePasswordResult.setCode("user.user.input.passwordnotcorrect");
            changePasswordResult.setMessage("密码错误");

            return changePasswordResult;
        }

        PasswordPolicy passwordPolicy = new PasswordPolicy();
        passwordPolicy.setUsername(accountInfo.getUsername());
        passwordPolicy.setOldPassword(oldPassword);

        List<String> keywords = new ArrayList<String>();
        passwordPolicy.setKeywords(keywords);

        if (passwordPolicy.validate(newPassword)) {
            changePasswordResult.setCode("user.user.input.passwordnotenough");
            changePasswordResult.setMessage("不满足密码要求");

            return changePasswordResult;
        }

        accountCredential.setPassword(encodePassword(newPassword));

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MONTH, 3);
        accountCredential.setModifyTime(now);
        accountCredential.setExpireTime(calendar.getTime());
        accountCredentialManager.save(accountCredential);

        changePasswordResult.setCode("core.success.save");
        changePasswordResult.setMessage("保存成功");
        changePasswordResult.setSuccess(true);

        return changePasswordResult;
    }

    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        if (customPasswordEncoder != null) {
            return customPasswordEncoder.matches(rawPassword, encodedPassword);
        } else {
            return rawPassword.equals(encodedPassword);
        }
    }

    public String encodePassword(String password) {
        if (customPasswordEncoder != null) {
            return customPasswordEncoder.encode(password);
        } else {
            return password;
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
