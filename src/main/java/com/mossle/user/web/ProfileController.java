package com.mossle.user.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.ServletUtils;

import com.mossle.ext.auth.CurrentUserHolder;

import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;
import com.mossle.user.persistence.manager.UserAttrManager;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.persistence.manager.UserSchemaManager;
import com.mossle.user.service.UserService;
import com.mossle.user.support.UserBaseWrapper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class ProfileController {
    private AccountInfoManager accountInfoManager;
    private PersonInfoManager personInfoManager;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();
    private UserBaseWrapper userBaseWrapper;
    private UserService userService;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("profile-list")
    public String list(Model model) {
        Long accountId = Long.parseLong(currentUserHolder.getUserId());
        AccountInfo accountInfo = accountInfoManager.get(accountId);
        PersonInfo personInfo = personInfoManager.findUniqueBy("code",
                accountInfo.getCode());
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("personInfo", personInfo);

        return "user/profile-list";
    }

    @RequestMapping("profile-save")
    public String save(@ModelAttribute PersonInfo personInfo,
            RedirectAttributes redirectAttributes) throws Exception {
        Long accountId = Long.parseLong(currentUserHolder.getUserId());
        AccountInfo accountInfo = accountInfoManager.get(accountId);
        PersonInfo dest = personInfoManager.findUniqueBy("code",
                accountInfo.getCode());

        if (dest != null) {
            beanMapper.copy(personInfo, dest);
        } else {
            dest = new PersonInfo();
            dest.setCode(accountInfo.getCode());
            beanMapper.copy(personInfo, dest);
        }

        personInfoManager.save(personInfo);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/profile-list.do";
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

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
