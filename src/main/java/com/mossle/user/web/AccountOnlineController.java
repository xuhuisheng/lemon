package com.mossle.user.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.user.persistence.domain.AccountOnline;
import com.mossle.user.persistence.manager.AccountOnlineManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("user")
public class AccountOnlineController {
    private AccountOnlineManager accountOnlineManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("account-online-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = accountOnlineManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "user/account-online-list";
    }

    @RequestMapping("account-online-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            AccountOnline accountOnline = accountOnlineManager.get(id);
            model.addAttribute("model", accountOnline);
        }

        return "user/account-online-input";
    }

    @RequestMapping("account-online-save")
    public String save(@ModelAttribute AccountOnline accountOnline,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        AccountOnline dest = null;

        Long id = accountOnline.getId();

        if (id != null) {
            dest = accountOnlineManager.get(id);
            beanMapper.copy(accountOnline, dest);
        } else {
            dest = accountOnline;
            dest.setTenantId(tenantId);
        }

        accountOnlineManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/user/account-online-list.do";
    }

    @RequestMapping("account-online-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<AccountOnline> accountOnlines = accountOnlineManager
                .findByIds(selectedItem);

        accountOnlineManager.removeAll(accountOnlines);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/user/account-online-list.do";
    }

    @RequestMapping("account-online-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = accountOnlineManager.pagedQuery(page, propertyFilters);

        List<AccountOnline> accountOnlines = (List<AccountOnline>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("account online");
        tableModel.addHeaders("id", "client", "server", "resource");
        tableModel.setData(accountOnlines);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setAccountOnlineManager(
            AccountOnlineManager accountOnlineManager) {
        this.accountOnlineManager = accountOnlineManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
