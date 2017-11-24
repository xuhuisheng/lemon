package com.mossle.internal.whitelist.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.internal.whitelist.persistence.domain.WhitelistApp;
import com.mossle.internal.whitelist.persistence.domain.WhitelistInfo;
import com.mossle.internal.whitelist.persistence.manager.WhitelistAppManager;
import com.mossle.internal.whitelist.persistence.manager.WhitelistInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("com.mossle.internal.whitelist.web.IndexController")
@RequestMapping("whitelist")
public class IndexController {
    private WhitelistInfoManager whitelistInfoManager;
    private WhitelistAppManager whitelistAppManager;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("index")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();

        // String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = whitelistInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "whitelist/index";
    }

    // ~ ======================================================================
    @Resource
    public void setWhitelistInfoManager(
            WhitelistInfoManager whitelistInfoManager) {
        this.whitelistInfoManager = whitelistInfoManager;
    }

    @Resource
    public void setWhitelistAppManager(WhitelistAppManager whitelistAppManager) {
        this.whitelistAppManager = whitelistAppManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
