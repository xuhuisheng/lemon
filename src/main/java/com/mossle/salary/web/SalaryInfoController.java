package com.mossle.salary.web;

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

import com.mossle.salary.persistence.domain.SalaryInfo;
import com.mossle.salary.persistence.manager.SalaryInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("salary")
public class SalaryInfoController {
    private SalaryInfoManager salaryInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("salary-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = salaryInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "salary/salary-info-list";
    }

    @RequestMapping("salary-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SalaryInfo salaryInfo = salaryInfoManager.get(id);
            model.addAttribute("model", salaryInfo);
        }

        return "salary/salary-info-input";
    }

    @RequestMapping("salary-info-save")
    public String save(@ModelAttribute SalaryInfo salaryInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        SalaryInfo dest = null;

        Long id = salaryInfo.getId();

        if (id != null) {
            dest = salaryInfoManager.get(id);
            beanMapper.copy(salaryInfo, dest);
        } else {
            dest = salaryInfo;
            dest.setTenantId(tenantId);
        }

        salaryInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/salary/salary-info-list.do";
    }

    @RequestMapping("salary-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SalaryInfo> salaryInfos = salaryInfoManager
                .findByIds(selectedItem);

        salaryInfoManager.removeAll(salaryInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/salary/salary-info-list.do";
    }

    @RequestMapping("salary-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = salaryInfoManager.pagedQuery(page, propertyFilters);

        List<SalaryInfo> salaryInfos = (List<SalaryInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("salary info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(salaryInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setSalaryInfoManager(SalaryInfoManager salaryInfoManager) {
        this.salaryInfoManager = salaryInfoManager;
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
