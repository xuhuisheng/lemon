package com.mossle.product.web;

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

import com.mossle.product.persistence.domain.ProductInfo;
import com.mossle.product.persistence.manager.ProductInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("product")
public class ProductInfoController {
    private ProductInfoManager productInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("product-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = productInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "product/product-info-list";
    }

    @RequestMapping("product-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ProductInfo productInfo = productInfoManager.get(id);
            model.addAttribute("model", productInfo);
        }

        return "product/product-info-input";
    }

    @RequestMapping("product-info-save")
    public String save(@ModelAttribute ProductInfo productInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        ProductInfo dest = null;

        Long id = productInfo.getId();

        if (id != null) {
            dest = productInfoManager.get(id);
            beanMapper.copy(productInfo, dest);
        } else {
            dest = productInfo;
            dest.setTenantId(tenantId);
        }

        productInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/product/product-info-list.do";
    }

    @RequestMapping("product-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ProductInfo> productInfos = productInfoManager
                .findByIds(selectedItem);

        productInfoManager.removeAll(productInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/product/product-info-list.do";
    }

    @RequestMapping("product-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = productInfoManager.pagedQuery(page, propertyFilters);

        List<ProductInfo> productInfos = (List<ProductInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("product info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(productInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setProductInfoManager(ProductInfoManager productInfoManager) {
        this.productInfoManager = productInfoManager;
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
