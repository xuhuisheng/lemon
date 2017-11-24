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

import com.mossle.internal.whitelist.persistence.domain.WhitelistService;
import com.mossle.internal.whitelist.persistence.manager.WhitelistServiceManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("whitelist")
public class WhitelistServiceController {
    private WhitelistServiceManager whitelistServiceManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("whitelist-service-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistServiceManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "whitelist/whitelist-service-list";
    }

    @RequestMapping("whitelist-service-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            WhitelistService whitelistService = whitelistServiceManager.get(id);
            model.addAttribute("model", whitelistService);
        }

        return "whitelist/whitelist-service-input";
    }

    @RequestMapping("whitelist-service-save")
    public String save(@ModelAttribute WhitelistService whitelistService,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = whitelistService.getId();
        WhitelistService dest = null;

        if (id != null) {
            dest = whitelistServiceManager.get(id);
            beanMapper.copy(whitelistService, dest);
        } else {
            dest = whitelistService;

            // dest.setTenantId(tenantId);
        }

        whitelistServiceManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/whitelist/whitelist-service-list.do";
    }

    @RequestMapping("whitelist-service-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<WhitelistService> whitelistServices = whitelistServiceManager
                .findByIds(selectedItem);
        whitelistServiceManager.removeAll(whitelistServices);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/whitelist/whitelist-service-list.do";
    }

    @RequestMapping("whitelist-service-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistServiceManager.pagedQuery(page, propertyFilters);

        List<WhitelistService> whitelistServices = (List<WhitelistService>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("whitelistService");
        tableModel.addHeaders("id", "name");
        tableModel.setData(whitelistServices);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setWhitelistServiceManager(
            WhitelistServiceManager whitelistServiceManager) {
        this.whitelistServiceManager = whitelistServiceManager;
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
