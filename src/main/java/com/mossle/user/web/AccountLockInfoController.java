package com.mossle.user.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountLockInfo;
import com.mossle.user.persistence.manager.AccountLockInfoManager;
import com.mossle.user.persistence.manager.AccountLockLogManager;
import com.mossle.user.service.AccountLockService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class AccountLockInfoController {
    private AccountLockInfoManager accountLockInfoManager;
    private AccountLockLogManager accountLockLogManager;
    private AccountLockService accountLockService;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("account-lock-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        page.setDefaultOrder("lockTime", "DESC");

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = accountLockInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "user/account-lock-info-list";
    }

    @RequestMapping("account-lock-info-unlock")
    public String unlock(@RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        AccountLockInfo accountLockInfo = accountLockInfoManager.get(id);

        String username = accountLockInfo.getUsername();
        String application = accountLockInfo.getType();
        accountLockService.unlock(username, application);
        messageHelper.addFlashMessage(redirectAttributes, "account.unlock",
                "解锁成功");

        return "redirect:/user/account-lock-info-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setAccountLockInfoManager(
            AccountLockInfoManager accountLockInfoManager) {
        this.accountLockInfoManager = accountLockInfoManager;
    }

    @Resource
    public void setAccountLockLogManager(
            AccountLockLogManager accountLockLogManager) {
        this.accountLockLogManager = accountLockLogManager;
    }

    @Resource
    public void setAccountLockService(AccountLockService accountLockService) {
        this.accountLockService = accountLockService;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
