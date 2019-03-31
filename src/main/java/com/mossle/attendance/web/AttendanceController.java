package com.mossle.attendance.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.attendance.persistence.domain.AttendanceInfo;
import com.mossle.attendance.persistence.manager.AttendanceInfoManager;
import com.mossle.attendance.service.AttendanceService;
import com.mossle.attendance.support.AttendanceDTO;
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
public class AttendanceController {
    private AttendanceInfoManager attendanceInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private AttendanceService attendanceService;

    @RequestMapping("index")
    public String index(Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        AttendanceDTO attendanceDto = this.attendanceService.findAttendanceStatus(userId);
        model.addAttribute("attendanceDto", attendanceDto);

        return "attendance/index";
    }

    @RequestMapping("widget")
    public String widget(Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        AttendanceDTO attendanceDto = this.attendanceService.findAttendanceStatus(userId);
        model.addAttribute("attendanceDto", attendanceDto);

        return "attendance/widget";
    }

    @RequestMapping("record")
    public String record() {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        attendanceService.saveRecord(userId, tenantId);

        return "redirect:/attendance/widget";
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

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setAttendanceService(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
}
