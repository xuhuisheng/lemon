package com.mossle.leave.web;

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

import com.mossle.leave.persistence.domain.LeaveInfo;
import com.mossle.leave.persistence.manager.LeaveInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("leave")
public class LeaveInfoController {
    private LeaveInfoManager leaveInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("leave-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = leaveInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "leave/leave-info-list";
    }

    @RequestMapping("leave-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            LeaveInfo leaveInfo = leaveInfoManager.get(id);
            model.addAttribute("model", leaveInfo);
        }

        return "leave/leave-info-input";
    }

    @RequestMapping("leave-info-save")
    public String save(@ModelAttribute LeaveInfo leaveInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        LeaveInfo dest = null;

        Long id = leaveInfo.getId();

        if (id != null) {
            dest = leaveInfoManager.get(id);
            beanMapper.copy(leaveInfo, dest);
        } else {
            dest = leaveInfo;
            dest.setTenantId(tenantId);
        }

        leaveInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/leave/leave-info-list.do";
    }

    @RequestMapping("leave-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<LeaveInfo> leaveInfos = leaveInfoManager.findByIds(selectedItem);

        leaveInfoManager.removeAll(leaveInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/leave/leave-info-list.do";
    }

    @RequestMapping("leave-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = leaveInfoManager.pagedQuery(page, propertyFilters);

        List<LeaveInfo> leaveInfos = (List<LeaveInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("leave info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(leaveInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setLeaveInfoManager(LeaveInfoManager leaveInfoManager) {
        this.leaveInfoManager = leaveInfoManager;
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
