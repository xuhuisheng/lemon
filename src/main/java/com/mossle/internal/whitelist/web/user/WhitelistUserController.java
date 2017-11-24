package com.mossle.internal.whitelist.web.user;

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

import com.mossle.internal.whitelist.persistence.domain.WhitelistApp;
import com.mossle.internal.whitelist.persistence.domain.WhitelistPackage;
import com.mossle.internal.whitelist.persistence.manager.WhitelistAppManager;
import com.mossle.internal.whitelist.persistence.manager.WhitelistPackageManager;
import com.mossle.internal.whitelist.service.WhitelistService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("com.mossle.internal.whitelist.web.user.WhitelistUserController")
@RequestMapping("whitelist/user")
public class WhitelistUserController {
    private WhitelistAppManager whitelistAppManager;
    private WhitelistService whitelistService;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;
    private MessageHelper messageHelper;

    @RequestMapping("apps")
    public String apps(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = whitelistAppManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "whitelist/user/apps";
    }

    @RequestMapping("app-input")
    public String appInput(
            @RequestParam(value = "id", required = false) Long id, Model model) {
        WhitelistApp whitelistApp = null;

        if (id != null) {
            whitelistApp = whitelistAppManager.get(id);
            model.addAttribute("model", whitelistApp);
        }

        return "whitelist/user/app-input";
    }

    @RequestMapping("save")
    public String save(@ModelAttribute WhitelistApp whitelistApp,
            @RequestParam("host") String hostContent,
            @RequestParam("ip") String ipContent,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        whitelistService.saveWhitelistApp(whitelistApp, 1L, hostContent,
                ipContent, userId, tenantId);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/whitelist/user/apps.do";
    }

    @RequestMapping("remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<WhitelistApp> whitelistApps = whitelistAppManager
                .findByIds(selectedItem);

        for (WhitelistApp whitelistApp : whitelistApps) {
            whitelistAppManager.removeAll(whitelistApp.getWhitelistHosts());
            whitelistAppManager.removeAll(whitelistApp.getWhitelistIps());
            whitelistAppManager.remove(whitelistApp);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/whitelist/user/apps.do";
    }

    // ~ ======================================================================
    @Resource
    public void setWhitelistAppManager(WhitelistAppManager whitelistAppManager) {
        this.whitelistAppManager = whitelistAppManager;
    }

    @Resource
    public void setWhitelistService(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
