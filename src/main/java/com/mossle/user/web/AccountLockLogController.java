package com.mossle.user.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountLockInfo;
import com.mossle.user.persistence.domain.AccountLockLog;
import com.mossle.user.persistence.manager.AccountLockInfoManager;
import com.mossle.user.persistence.manager.AccountLockLogManager;

import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class AccountLockLogController {
    private AccountLockLogManager accountLockLogManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("account-lock-log-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        page.setDefaultOrder("lockTime", "DESC");

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = accountLockLogManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "user/account-lock-log-list";
    }

    // ~ ======================================================================
    @Resource
    public void setAccountLockLogManager(
            AccountLockLogManager accountLockLogManager) {
        this.accountLockLogManager = accountLockLogManager;
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
