package com.mossle.visitor.web;

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

import com.mossle.visitor.persistence.domain.VisitorInfo;
import com.mossle.visitor.persistence.manager.VisitorInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("visitor")
public class VisitorInfoController {
    private VisitorInfoManager visitorInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("visitor-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = visitorInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "visitor/visitor-info-list";
    }

    @RequestMapping("visitor-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            VisitorInfo visitorInfo = visitorInfoManager.get(id);
            model.addAttribute("model", visitorInfo);
        }

        return "visitor/visitor-info-input";
    }

    @RequestMapping("visitor-info-save")
    public String save(@ModelAttribute VisitorInfo visitorInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        VisitorInfo dest = null;

        Long id = visitorInfo.getId();

        if (id != null) {
            dest = visitorInfoManager.get(id);
            beanMapper.copy(visitorInfo, dest);
        } else {
            dest = visitorInfo;
            dest.setTenantId(tenantId);
        }

        visitorInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/visitor/visitor-info-list.do";
    }

    @RequestMapping("visitor-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<VisitorInfo> visitorInfos = visitorInfoManager
                .findByIds(selectedItem);

        visitorInfoManager.removeAll(visitorInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/visitor/visitor-info-list.do";
    }

    @RequestMapping("visitor-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = visitorInfoManager.pagedQuery(page, propertyFilters);

        List<VisitorInfo> visitorInfos = (List<VisitorInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("visitor info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(visitorInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setVisitorInfoManager(VisitorInfoManager visitorInfoManager) {
        this.visitorInfoManager = visitorInfoManager;
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
