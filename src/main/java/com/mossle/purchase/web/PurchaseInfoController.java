package com.mossle.purchase.web;

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

import com.mossle.purchase.persistence.domain.PurchaseInfo;
import com.mossle.purchase.persistence.manager.PurchaseInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("purchase")
public class PurchaseInfoController {
    private PurchaseInfoManager purchaseInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("purchase-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = purchaseInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "purchase/purchase-info-list";
    }

    @RequestMapping("purchase-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PurchaseInfo purchaseInfo = purchaseInfoManager.get(id);
            model.addAttribute("model", purchaseInfo);
        }

        return "purchase/purchase-info-input";
    }

    @RequestMapping("purchase-info-save")
    public String save(@ModelAttribute PurchaseInfo purchaseInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        PurchaseInfo dest = null;

        Long id = purchaseInfo.getId();

        if (id != null) {
            dest = purchaseInfoManager.get(id);
            beanMapper.copy(purchaseInfo, dest);
        } else {
            dest = purchaseInfo;
            dest.setTenantId(tenantId);
        }

        purchaseInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/purchase/purchase-info-list.do";
    }

    @RequestMapping("purchase-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PurchaseInfo> purchaseInfos = purchaseInfoManager
                .findByIds(selectedItem);

        purchaseInfoManager.removeAll(purchaseInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/purchase/purchase-info-list.do";
    }

    @RequestMapping("purchase-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = purchaseInfoManager.pagedQuery(page, propertyFilters);

        List<PurchaseInfo> purchaseInfos = (List<PurchaseInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("purchase info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(purchaseInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPurchaseInfoManager(PurchaseInfoManager purchaseInfoManager) {
        this.purchaseInfoManager = purchaseInfoManager;
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
