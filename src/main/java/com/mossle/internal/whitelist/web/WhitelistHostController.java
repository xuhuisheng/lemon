package com.mossle.internal.whitelist.web;

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

import com.mossle.internal.whitelist.persistence.domain.WhitelistHost;
import com.mossle.internal.whitelist.persistence.manager.WhitelistHostManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("whitelist")
public class WhitelistHostController {
    private WhitelistHostManager whitelistHostManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("whitelist-host-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistHostManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "whitelist/whitelist-host-list";
    }

    @RequestMapping("whitelist-host-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            WhitelistHost whitelistHost = whitelistHostManager.get(id);
            model.addAttribute("model", whitelistHost);
        }

        return "whitelist/whitelist-host-input";
    }

    @RequestMapping("whitelist-host-save")
    public String save(@ModelAttribute WhitelistHost whitelistHost,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = whitelistHost.getId();
        WhitelistHost dest = null;

        if (id != null) {
            dest = whitelistHostManager.get(id);
            beanMapper.copy(whitelistHost, dest);
        } else {
            dest = whitelistHost;
            dest.setTenantId(tenantId);
        }

        whitelistHostManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/whitelist/whitelist-host-list.do";
    }

    @RequestMapping("whitelist-host-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<WhitelistHost> whitelistHosts = whitelistHostManager
                .findByIds(selectedItem);
        whitelistHostManager.removeAll(whitelistHosts);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/whitelist/whitelist-host-list.do";
    }

    @RequestMapping("whitelist-host-export")
    public void export(@ModelAttribute Page page, HttpServletRequest request,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistHostManager.pagedQuery(page, propertyFilters);

        List<WhitelistHost> whitelistHosts = (List<WhitelistHost>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("whitelistHost");
        tableModel.addHeaders("id", "name");
        tableModel.setData(whitelistHosts);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setWhitelistHostManager(
            WhitelistHostManager whitelistHostManager) {
        this.whitelistHostManager = whitelistHostManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
