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
import com.mossle.user.service.ChangePasswordService;
import com.mossle.user.support.ChangePasswordResult;

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
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private ChangePasswordService changePasswordService;

    @RequestMapping("change-password-input")
    public String input() {
        return "user/change-password-input";
    }

    @RequestMapping("change-password-save")
    public String save(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        Long accountId = Long.parseLong(currentUserHolder.getUserId());
        ChangePasswordResult changePasswordResult = changePasswordService
                .changePassword(accountId, oldPassword, newPassword,
                        confirmPassword);

        if (changePasswordResult.isSuccess()) {
            messageHelper.addFlashMessage(redirectAttributes,
                    changePasswordResult.getCode(),
                    changePasswordResult.getMessage());

            return "redirect:/user/change-password-input.do";
        } else {
            messageHelper.addFlashMessage(redirectAttributes,
                    changePasswordResult.getCode(),
                    changePasswordResult.getMessage());

            return "redirect:/user/change-password-input.do";
        }
    }

    // ~ ======================================================================
    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setChangePasswordService(
            ChangePasswordService changePasswordService) {
        this.changePasswordService = changePasswordService;
    }
}
