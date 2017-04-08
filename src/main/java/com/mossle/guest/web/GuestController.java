package com.mossle.guest.web;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;

import com.mossle.core.spring.MessageHelper;

import com.mossle.user.service.ChangePasswordService;
import com.mossle.user.support.ChangePasswordResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("guest")
public class GuestController {
    private static Logger logger = LoggerFactory
            .getLogger(GuestController.class);
    private MessageHelper messageHelper;
    private ChangePasswordService changePasswordService;

    @RequestMapping("change-password-input")
    public String changePasswordInput(HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute(
                "SECURITY_LAST_USERNAME");

        if (username == null) {
            logger.info("username is null");

            return "redirect:/common/login.jsp";
        }

        return "guest/change-password-input";
    }

    @RequestMapping("change-password-save")
    public String changePasswordSave(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpServletRequest request, RedirectAttributes redirectAttributes)
            throws Exception {
        String username = (String) request.getSession().getAttribute(
                "SECURITY_LAST_USERNAME");

        if (username == null) {
            logger.info("username is null");

            return "redirect:/common/login.jsp";
        }

        ChangePasswordResult changePasswordResult = changePasswordService
                .changePassword(username, oldPassword, newPassword,
                        confirmPassword);

        if (changePasswordResult.isSuccess()) {
            messageHelper.addFlashMessage(redirectAttributes,
                    changePasswordResult.getCode(),
                    changePasswordResult.getMessage());

            return "redirect:/guest/change-password-success.do";
        } else {
            messageHelper.addFlashMessage(redirectAttributes,
                    changePasswordResult.getCode(),
                    changePasswordResult.getMessage());

            return "redirect:/guest/change-password-input.do";
        }
    }

    @RequestMapping("change-password-success")
    public String changePasswordSuccess() {
        return "guest/change-password-success";
    }

    // ~ ======================================================================
    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setChangePasswordService(
            ChangePasswordService changePasswordService) {
        this.changePasswordService = changePasswordService;
    }
}
