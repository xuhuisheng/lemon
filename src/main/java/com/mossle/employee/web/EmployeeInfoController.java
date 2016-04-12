package com.mossle.employee.web;

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

import com.mossle.employee.persistence.domain.EmployeeInfo;
import com.mossle.employee.persistence.manager.EmployeeInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("employee")
public class EmployeeInfoController {
    private EmployeeInfoManager employeeInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("employee-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = employeeInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "employee/employee-info-list";
    }

    @RequestMapping("employee-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            EmployeeInfo employeeInfo = employeeInfoManager.get(id);
            model.addAttribute("model", employeeInfo);
        }

        return "employee/employee-info-input";
    }

    @RequestMapping("employee-info-save")
    public String save(@ModelAttribute EmployeeInfo employeeInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        EmployeeInfo dest = null;

        Long id = employeeInfo.getId();

        if (id != null) {
            dest = employeeInfoManager.get(id);
            beanMapper.copy(employeeInfo, dest);
        } else {
            dest = employeeInfo;
            dest.setTenantId(tenantId);
        }

        employeeInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/employee/employee-info-list.do";
    }

    @RequestMapping("employee-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<EmployeeInfo> employeeInfos = employeeInfoManager
                .findByIds(selectedItem);

        employeeInfoManager.removeAll(employeeInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/employee/employee-info-list.do";
    }

    @RequestMapping("employee-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = employeeInfoManager.pagedQuery(page, propertyFilters);

        List<EmployeeInfo> employeeInfos = (List<EmployeeInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("employee info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(employeeInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setEmployeeInfoManager(EmployeeInfoManager employeeInfoManager) {
        this.employeeInfoManager = employeeInfoManager;
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
