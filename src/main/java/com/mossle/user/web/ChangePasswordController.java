package com.mossle.user.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.auth.CurrentUserHolder;
import com.mossle.ext.auth.CustomPasswordEncoder;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.UserBaseManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class ChangePasswordController {
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private MessageHelper messageHelper;
    private CustomPasswordEncoder customPasswordEncoder;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("change-password-input")
    public String input() {
        return "user/change-password-input";
    }

    @RequestMapping("change-password-save")
    public String save(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            messageHelper.addFlashMessage(redirectAttributes,
                    "user.user.input.passwordnotequals", "两次输入密码不符");

            return "redirect:/user/change-password-input.do";
        }

        Long accountId = Long.parseLong(currentUserHolder.getUserId());
        AccountInfo accountInfo = accountInfoManager.get(accountId);
        String hql = "from AccountCredential where accountInfo=? and catalog='default'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, accountInfo);

        if (accountCredential == null) {
            messageHelper.addFlashMessage(redirectAttributes,
                    "user.user.input.credentialnotexists", "未设置过密码");

            return "redirect:/user/change-password-input.do";
        }

        if (!isPasswordValid(oldPassword, accountCredential.getPassword())) {
            messageHelper.addFlashMessage(redirectAttributes,
                    "user.user.input.passwordnotcorrect", "密码错误");

            return "redirect:/user/change-password-input.do";
        }

        accountCredential.setPassword(encodePassword(newPassword));
        accountCredentialManager.save(accountCredential);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/change-password-input.do";
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
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
