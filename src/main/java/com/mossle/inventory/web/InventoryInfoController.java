package com.mossle.inventory.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.inventory.persistence.domain.InventoryInfo;
import com.mossle.inventory.persistence.manager.InventoryInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("inventory")
public class InventoryInfoController {
    private InventoryInfoManager inventoryInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("inventory-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = inventoryInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "inventory/inventory-info-list";
    }

    @RequestMapping("inventory-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            InventoryInfo inventoryInfo = inventoryInfoManager.get(id);
            model.addAttribute("model", inventoryInfo);
        }

        return "inventory/inventory-info-input";
    }

    @RequestMapping("inventory-info-save")
    public String save(@ModelAttribute InventoryInfo inventoryInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        InventoryInfo dest = null;

        Long id = inventoryInfo.getId();

        if (id != null) {
            dest = inventoryInfoManager.get(id);
            beanMapper.copy(inventoryInfo, dest);
        } else {
            dest = inventoryInfo;
            dest.setTenantId(tenantId);
        }

        inventoryInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/inventory/inventory-info-list.do";
    }

    @RequestMapping("inventory-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<InventoryInfo> inventoryInfos = inventoryInfoManager
                .findByIds(selectedItem);

        inventoryInfoManager.removeAll(inventoryInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/inventory/inventory-info-list.do";
    }

    @RequestMapping("inventory-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = inventoryInfoManager.pagedQuery(page, propertyFilters);

        List<InventoryInfo> inventoryInfos = (List<InventoryInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("inventory info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(inventoryInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setInventoryInfoManager(
            InventoryInfoManager inventoryInfoManager) {
        this.inventoryInfoManager = inventoryInfoManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
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
