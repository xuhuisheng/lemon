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

import com.mossle.internal.whitelist.persistence.domain.WhitelistType;
import com.mossle.internal.whitelist.persistence.manager.WhitelistTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("whitelist")
public class WhitelistTypeController {
    private WhitelistTypeManager whitelistTypeManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("whitelist-type-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistTypeManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "whitelist/whitelist-type-list";
    }

    @RequestMapping("whitelist-type-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            WhitelistType whitelistType = whitelistTypeManager.get(id);
            model.addAttribute("model", whitelistType);
        }

        return "whitelist/whitelist-type-input";
    }

    @RequestMapping("whitelist-type-save")
    public String save(@ModelAttribute WhitelistType whitelistType,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = whitelistType.getId();
        WhitelistType dest = null;

        if (id != null) {
            dest = whitelistTypeManager.get(id);
            beanMapper.copy(whitelistType, dest);
        } else {
            dest = whitelistType;
            dest.setTenantId(tenantId);
        }

        whitelistTypeManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/whitelist/whitelist-type-list.do";
    }

    @RequestMapping("whitelist-type-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<WhitelistType> whitelistTypes = whitelistTypeManager
                .findByIds(selectedItem);
        whitelistTypeManager.removeAll(whitelistTypes);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/whitelist/whitelist-type-list.do";
    }

    @RequestMapping("whitelist-type-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = whitelistTypeManager.pagedQuery(page, propertyFilters);

        List<WhitelistType> whitelistTypes = (List<WhitelistType>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("whitelistType");
        tableModel.addHeaders("id", "name");
        tableModel.setData(whitelistTypes);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setWhitelistTypeManager(
            WhitelistTypeManager whitelistTypeManager) {
        this.whitelistTypeManager = whitelistTypeManager;
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
