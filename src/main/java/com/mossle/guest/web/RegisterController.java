package com.mossle.guest.web;

import javax.annotation.Resource;

import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("guest")
public class RegisterController {
    private static Logger logger = LoggerFactory
            .getLogger(RegisterController.class);
    private MessageHelper messageHelper;
    private AccountInfoManager accountInfoManager;

    @RequestMapping("register-view")
    public String registerView() {
        return "guest/registerView";
    }

    @RequestMapping("registerSave")
    public String registerSave(@ModelAttribute AccountInfo accountInfo) {
        accountInfo.setStatus("disabled");
        accountInfo.setType("register");
        accountInfoManager.save(accountInfo);

        return "redirect:/guest/register-success.do";
    }

    // ~ ======================================================================
    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }
}
