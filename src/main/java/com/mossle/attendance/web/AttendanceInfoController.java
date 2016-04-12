package com.mossle.attendance.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.attendance.persistence.domain.AttendanceInfo;
import com.mossle.attendance.persistence.manager.AttendanceInfoManager;

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
public class AttendanceInfoController {
    private AttendanceInfoManager attendanceInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("attendance-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = attendanceInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "attendance/attendance-info-list";
    }

    @RequestMapping("attendance-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            AttendanceInfo attendanceInfo = attendanceInfoManager.get(id);
            model.addAttribute("model", attendanceInfo);
        }

        return "attendance/attendance-info-input";
    }

    @RequestMapping("attendance-info-save")
    public String save(@ModelAttribute AttendanceInfo attendanceInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        AttendanceInfo dest = null;

        Long id = attendanceInfo.getId();

        if (id != null) {
            dest = attendanceInfoManager.get(id);
            beanMapper.copy(attendanceInfo, dest);
        } else {
            dest = attendanceInfo;
            dest.setTenantId(tenantId);
        }

        attendanceInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/attendance/attendance-info-list.do";
    }

    @RequestMapping("attendance-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<AttendanceInfo> attendanceInfos = attendanceInfoManager
                .findByIds(selectedItem);

        attendanceInfoManager.removeAll(attendanceInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/attendance/attendance-info-list.do";
    }

    @RequestMapping("attendance-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = attendanceInfoManager.pagedQuery(page, propertyFilters);

        List<AttendanceInfo> attendanceInfos = (List<AttendanceInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("attendance info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(attendanceInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setAttendanceInfoManager(
            AttendanceInfoManager attendanceInfoManager) {
        this.attendanceInfoManager = attendanceInfoManager;
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
