package com.mossle.user.web.user;

import javax.annotation.Resource;

import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户站.
 */
@Controller
@RequestMapping("user/user")
public class UserUserController {
    private static Logger logger = LoggerFactory
            .getLogger(UserUserController.class);
    private AccountInfoManager accountInfoManager;
    private PersonInfoManager personInfoManager;

    /**
     * 显示个人信息.
     * 
     * @param model
     *            Model
     * @return String
     */
    @RequestMapping("view")
    public String view(@RequestParam("username") String username, Model model) {
        logger.debug("view : {}", username);

        AccountInfo accountInfo = this.accountInfoManager.findUniqueBy(
                "username", username);
        PersonInfo personInfo = personInfoManager.findUniqueBy("code",
                accountInfo.getCode());
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("personInfo", personInfo);

        return "user/user/view";
    }

    // ~ ======================================================================
    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }
}
