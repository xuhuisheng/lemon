package com.mossle.sale.web;

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

import com.mossle.sale.persistence.domain.SaleInfo;
import com.mossle.sale.persistence.manager.SaleInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("sale")
public class SaleInfoController {
    private SaleInfoManager saleInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("sale-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = saleInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "sale/sale-info-list";
    }

    @RequestMapping("sale-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SaleInfo saleInfo = saleInfoManager.get(id);
            model.addAttribute("model", saleInfo);
        }

        return "sale/sale-info-input";
    }

    @RequestMapping("sale-info-save")
    public String save(@ModelAttribute SaleInfo saleInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        SaleInfo dest = null;

        Long id = saleInfo.getId();

        if (id != null) {
            dest = saleInfoManager.get(id);
            beanMapper.copy(saleInfo, dest);
        } else {
            dest = saleInfo;
            dest.setTenantId(tenantId);
        }

        saleInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sale/sale-info-list.do";
    }

    @RequestMapping("sale-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SaleInfo> saleInfos = saleInfoManager.findByIds(selectedItem);

        saleInfoManager.removeAll(saleInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/sale/sale-info-list.do";
    }

    @RequestMapping("sale-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = saleInfoManager.pagedQuery(page, propertyFilters);

        List<SaleInfo> saleInfos = (List<SaleInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("sale info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(saleInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setSaleInfoManager(SaleInfoManager saleInfoManager) {
        this.saleInfoManager = saleInfoManager;
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
