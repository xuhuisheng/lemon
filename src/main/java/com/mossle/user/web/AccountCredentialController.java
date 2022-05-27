package com.mossle.user.web;

import javax.annotation.Resource;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.service.UserService;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("user")
public class AccountCredentialController {
    private AccountCredentialManager accountCredentialManager;
    private UserService userService;

    @RequestMapping("account-credential-generate")
    public String generate(@RequestParam("id") Long id) {
        AccountCredential accountCredential = this.accountCredentialManager
                .get(id);
        String password = userService.generatePassword(id);

        return "redirect:/user/account-detail-password.do?id=" + id
                + "&infoId=" + accountCredential.getAccountInfo().getId()
                + "&password=" + password;
    }

    // ~ ======================================================================
    @Resource
    public void setAccountCredentialManager(
            AccountCredentialManager accountCredentialManager) {
        this.accountCredentialManager = accountCredentialManager;
    }

    @Resource
    public void setUserServcie(UserService userService) {
        this.userService = userService;
    }
}
