package com.mossle.user.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.spring.MessageHelper;

import com.mossle.security.util.SimplePasswordEncoder;
import com.mossle.security.util.SpringSecurityUtils;

import com.mossle.user.persistence.domain.UserBase;
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
    private UserBaseManager userBaseManager;
    private MessageHelper messageHelper;
    private SimplePasswordEncoder simplePasswordEncoder;

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

        UserBase userBase = userBaseManager.findUniqueBy("username",
                SpringSecurityUtils.getCurrentUsername());

        if (!isPasswordValid(oldPassword, userBase.getPassword())) {
            messageHelper.addFlashMessage(redirectAttributes,
                    "user.user.input.passwordnotcorrect", "密码错误");

            return "redirect:/user/change-password-input.do";
        }

        userBase.setPassword(encodePassword(newPassword));
        userBaseManager.save(userBase);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/change-password-input.do";
    }

    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        if (simplePasswordEncoder != null) {
            return simplePasswordEncoder.matches(rawPassword, encodedPassword);
        } else {
            return rawPassword.equals(encodedPassword);
        }
    }

    public String encodePassword(String password) {
        if (simplePasswordEncoder != null) {
            return simplePasswordEncoder.encode(password);
        } else {
            return password;
        }
    }

    // ~ ======================================================================
    @Resource
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setSimplePasswordEncoder(
            SimplePasswordEncoder simplePasswordEncoder) {
        this.simplePasswordEncoder = simplePasswordEncoder;
    }
}
