package com.mossle.performance.web;

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

import com.mossle.performance.persistence.domain.PerformanceInfo;
import com.mossle.performance.persistence.manager.PerformanceInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("performance")
public class PerformanceInfoController {
    private PerformanceInfoManager performanceInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("performance-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = performanceInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "performance/performance-info-list";
    }

    @RequestMapping("performance-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PerformanceInfo performanceInfo = performanceInfoManager.get(id);
            model.addAttribute("model", performanceInfo);
        }

        return "performance/performance-info-input";
    }

    @RequestMapping("performance-info-save")
    public String save(@ModelAttribute PerformanceInfo performanceInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        PerformanceInfo dest = null;

        Long id = performanceInfo.getId();

        if (id != null) {
            dest = performanceInfoManager.get(id);
            beanMapper.copy(performanceInfo, dest);
        } else {
            dest = performanceInfo;
            dest.setTenantId(tenantId);
        }

        performanceInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/performance/performance-info-list.do";
    }

    @RequestMapping("performance-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PerformanceInfo> performanceInfos = performanceInfoManager
                .findByIds(selectedItem);

        performanceInfoManager.removeAll(performanceInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/performance/performance-info-list.do";
    }

    @RequestMapping("performance-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = performanceInfoManager.pagedQuery(page, propertyFilters);

        List<PerformanceInfo> performanceInfos = (List<PerformanceInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("performance info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(performanceInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPerformanceInfoManager(
            PerformanceInfoManager performanceInfoManager) {
        this.performanceInfoManager = performanceInfoManager;
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
