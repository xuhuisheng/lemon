package com.mossle.business.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.business.persistence.domain.BusinessInfo;
import com.mossle.business.persistence.manager.BusinessInfoManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("business")
public class BusinessInfoController {
    private BusinessInfoManager businessInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("business-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = businessInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "business/business-info-list";
    }

    @RequestMapping("business-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            BusinessInfo businessInfo = businessInfoManager.get(id);
            model.addAttribute("model", businessInfo);
        }

        return "business/business-info-input";
    }

    @RequestMapping("business-info-save")
    public String save(@ModelAttribute BusinessInfo businessInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        BusinessInfo dest = null;

        Long id = businessInfo.getId();

        if (id != null) {
            dest = businessInfoManager.get(id);
            beanMapper.copy(businessInfo, dest);
        } else {
            dest = businessInfo;
            dest.setTenantId(tenantId);
        }

        businessInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/business/business-info-list.do";
    }

    @RequestMapping("business-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<BusinessInfo> businessInfos = businessInfoManager
                .findByIds(selectedItem);

        businessInfoManager.removeAll(businessInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/business/business-info-list.do";
    }

    @RequestMapping("business-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = businessInfoManager.pagedQuery(page, propertyFilters);

        List<BusinessInfo> businessInfos = (List<BusinessInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("business info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(businessInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setBusinessInfoManager(BusinessInfoManager businessInfoManager) {
        this.businessInfoManager = businessInfoManager;
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
