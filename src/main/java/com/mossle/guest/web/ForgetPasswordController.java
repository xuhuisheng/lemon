package com.mossle.guest.web;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;

import com.mossle.core.id.IdGenerator;
import com.mossle.core.spring.MessageHelper;

import com.mossle.spi.user.InternalUserConnector;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("guest")
public class ForgetPasswordController {
    private static Logger logger = LoggerFactory
            .getLogger(ForgetPasswordController.class);
    private MessageHelper messageHelper;
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private PersonInfoManager personInfoManager;
    private CustomPasswordEncoder customPasswordEncoder;
    private IdGenerator idGenerator;
    private InternalUserConnector internalUserConnector;

    @RequestMapping("forget-password-view")
    public String forgetPasswordView() {
        return "guest/forget-password-view";
    }

    @RequestMapping("forget-password-request")
    public String forgetPasswordRequest(@RequestParam("email") String email,
            @RequestParam("captcha") String captcha, Model model) {
        // String username = this.accountAliasConnector.findUsernameByAlias(email);
        PersonInfo personInfo = this.personInfoManager.findUniqueBy("email",
                email);

        if (personInfo == null) {
            logger.info("cannot find user : {}", email);

            return "guest/forget-password-request-failure";
        }

        String username = personInfo.getUsername();
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);

        if (accountInfo == null) {
            logger.info("cannot find user : {}", username);

            return "guest/forget-password-request-failure";
        }

        String hql = "from AccountCredential where catalog='forget' and accountInfo=?";
        AccountCredential accountCredential = this.accountCredentialManager
                .findUnique(hql, accountInfo);

        if (accountCredential == null) {
            accountCredential = new AccountCredential();
            accountCredential.setAccountInfo(accountInfo);
            accountCredential.setCatalog("forget");
            accountCredential.setType("uuid");
        }

        String code = UUID.randomUUID().toString();
        accountCredential.setPassword(code);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 12);
        accountCredential.setExpireTime(calendar.getTime());
        accountCredentialManager.save(accountCredential);
        // TODO: sendmail
        logger.info("email : {}", email);
        logger.info("http://localhost:8080/mossle-app-user/guest/forget-password-confirm-view.do?code="
                + code);

        return "redirect:/guest/forget-password-request-success.do";
    }

    @RequestMapping("forget-password-request-success")
    public String forgetPasswordRequestSuccess() {
        return "guest/forget-password-request-success";
    }

    @RequestMapping("forget-password-confirm-view")
    public String forgetPasswordConfirmView(@RequestParam("code") String code,
            Model model) {
        String hql = "from AccountCredential where catalog='forget' and password=?";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, code);

        if (accountCredential == null) {
            logger.info("cannot find user : {}", code);

            return "guest/forget-password-request-failure";
        }

        // TODO: validate
        model.addAttribute("accountInfo", accountCredential.getAccountInfo());

        return "guest/forget-password-confirm-view";
    }

    @RequestMapping("forget-password-confirm-save")
    public String forgetPasswordConfirmSave(@RequestParam("code") String code,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword) {
        String hql = "from AccountCredential where catalog='forget' and password=?";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, code);

        if (accountCredential == null) {
            logger.info("cannot find user : {}", code);

            return "guest/forget-password-request-failure";
        }

        // TODO: validate
        AccountInfo accountInfo = accountCredential.getAccountInfo();
        accountCredentialManager.remove(accountCredential);

        hql = "from AccountCredential where catalog='default' and accountInfo=?";
        accountCredential = accountCredentialManager.findUnique(hql,
                accountInfo);

        accountCredential
                .setPassword(customPasswordEncoder.encode(newPassword));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 90);
        accountCredential.setExpireTime(calendar.getTime());
        accountCredentialManager.save(accountCredential);

        return "redirect:/guest/forget-password-confirm-success.do";
    }

    @RequestMapping("forget-password-confirm-success")
    public String forgetPasswordConfirmSuccess() {
        return "guest/forget-password-confirm-success";
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

    @Resource
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Resource
    public void setInternalUserConnector(
            InternalUserConnector internalUserConnector) {
        this.internalUserConnector = internalUserConnector;
    }

    @Resource
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }
}
