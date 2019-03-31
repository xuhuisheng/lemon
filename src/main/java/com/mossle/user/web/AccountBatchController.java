package com.mossle.user.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountAvatar;
import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountAvatarManager;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;
import com.mossle.user.publish.UserPublisher;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class AccountBatchController {
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private AccountAvatarManager accountAvatarManager;
    private PersonInfoManager personInfoManager;
    private UserCache userCache;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CustomPasswordEncoder customPasswordEncoder;
    private UserPublisher userPublisher;
    private TenantHolder tenantHolder;

    @RequestMapping("account-batch-input")
    public String input(@RequestParam("selectedItem") List<Long> selectedItem,
            Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<AccountInfo> accountInfos = accountInfoManager
                .findByIds(selectedItem);

        model.addAttribute("accountInfos", accountInfos);

        return "user/account-batch-input";
    }

    @RequestMapping("account-batch-save")
    public String save(@RequestParam("selectedItem") List<Long> selectedItem,
            @RequestParam("type") List<String> types,
            @RequestParam("value") List<String> values) {
        List<AccountInfo> accountInfos = accountInfoManager
                .findByIds(selectedItem);
        List<String> usernames = new ArrayList<String>();

        for (AccountInfo accountInfo : accountInfos) {
            usernames.add(accountInfo.getUsername());
        }

        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            String value = values.get(i);

            if ("status".equals(type)) {
                for (AccountInfo accountInfo : accountInfos) {
                    accountInfo.setStatus(value);
                    accountInfoManager.save(accountInfo);
                }
            } else if ("type".equals(type)) {
                for (AccountInfo accountInfo : accountInfos) {
                    accountInfo.setType(value);
                    accountInfoManager.save(accountInfo);
                }
            }
        }

        return "redirect:/user/account-info-list.do?filter_INS_username="
                + StringUtils.join(usernames, ",");
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
    public void setAccountAvatarManager(
            AccountAvatarManager accountAvatarManager) {
        this.accountAvatarManager = accountAvatarManager;
    }

    @Resource
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }

    @Resource
    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
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
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserPublisher(UserPublisher userPublisher) {
        this.userPublisher = userPublisher;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
