package com.mossle.user.web.user;

import com.mossle.core.struts2.BaseAction;

import com.mossle.security.util.SimplePasswordEncoder;
import com.mossle.security.util.SpringSecurityUtils;

import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.manager.UserBaseManager;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({ @Result(name = ChangePasswordAction.RELOAD, location = "change-password.do?operationMode=RETRIEVE", type = "redirect") })
public class ChangePasswordAction extends BaseAction {
    public static final String RELOAD = "reload";
    private UserBaseManager userBaseManager;
    private MessageSourceAccessor messages;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private SimplePasswordEncoder simplePasswordEncoder;

    public String execute() {
        return input();
    }

    public String save() {
        if (!newPassword.equals(confirmPassword)) {
            addActionMessage(messages.getMessage(
                    "user.user.input.passwordnotequals", "两次输入密码不符"));

            return INPUT;
        }

        UserBase userBase = userBaseManager.findUniqueBy("username",
                SpringSecurityUtils.getCurrentUsername());

        if (!isPasswordValid(oldPassword, userBase.getPassword())) {
            addActionMessage(messages.getMessage(
                    "user.user.input.passwordnotcorrect", "密码错误"));

            return INPUT;
        }

        userBase.setPassword(encodePassword(newPassword));
        userBaseManager.save(userBase);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String input() {
        return INPUT;
    }

    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        if (simplePasswordEncoder != null) {
            return simplePasswordEncoder.matches(oldPassword, encodedPassword);
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
    public void setUserBaseManager(UserBaseManager userBaseManager) {
        this.userBaseManager = userBaseManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setSimplePasswordEncoder(
            SimplePasswordEncoder simplePasswordEncoder) {
        this.simplePasswordEncoder = simplePasswordEncoder;
    }

    // ~ ======================================================================
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
