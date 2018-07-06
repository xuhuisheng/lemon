package com.mossle.attendance.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.attendance.persistence.domain.AttendanceRule;
import com.mossle.attendance.persistence.manager.AttendanceRuleManager;

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
@RequestMapping("attendance")
public class AttendanceRuleController {
    private AttendanceRuleManager attendanceRuleManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("attendance-rule-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = attendanceRuleManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "attendance/attendance-rule-list";
    }

    @RequestMapping("attendance-rule-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            AttendanceRule attendanceRule = attendanceRuleManager.get(id);
            model.addAttribute("model", attendanceRule);
        }

        return "attendance/attendance-rule-input";
    }

    @RequestMapping("attendance-rule-save")
    public String save(@ModelAttribute AttendanceRule attendanceRule,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        AttendanceRule dest = null;

        Long id = attendanceRule.getId();

        if (id != null) {
            dest = attendanceRuleManager.get(id);
            beanMapper.copy(attendanceRule, dest);
        } else {
            dest = attendanceRule;
            dest.setTenantId(tenantId);
        }

        attendanceRuleManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/attendance/attendance-rule-list.do";
    }

    @RequestMapping("attendance-rule-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<AttendanceRule> attendanceRules = attendanceRuleManager
                .findByIds(selectedItem);

        attendanceRuleManager.removeAll(attendanceRules);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/attendance/attendance-rule-list.do";
    }

    @RequestMapping("attendance-rule-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = attendanceRuleManager.pagedQuery(page, propertyFilters);

        List<AttendanceRule> attendanceRules = (List<AttendanceRule>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("attendance info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(attendanceRules);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setAttendanceRuleManager(
            AttendanceRuleManager attendanceRuleManager) {
        this.attendanceRuleManager = attendanceRuleManager;
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
