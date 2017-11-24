package com.mossle.internal.whitelist.web.admin;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.internal.whitelist.persistence.domain.WhitelistPackage;
import com.mossle.internal.whitelist.persistence.manager.WhitelistPackageManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("com.mossle.internal.whitelist.web.admin.WhitelistAdminController")
@RequestMapping("whitelist/admin")
public class WhitelistAdminController {
    private WhitelistPackageManager whitelistPackageManager;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("packages")
    public String packages(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = whitelistPackageManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "whitelist/admin/packages";
    }

    // ~ ======================================================================
    @Resource
    public void setWhitelistPackageManager(
            WhitelistPackageManager whitelistPackageManager) {
        this.whitelistPackageManager = whitelistPackageManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
