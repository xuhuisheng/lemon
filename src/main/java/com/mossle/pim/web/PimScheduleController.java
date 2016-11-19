package com.mossle.pim.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.pim.persistence.domain.PimSchedule;
import com.mossle.pim.persistence.manager.PimScheduleManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pim")
public class PimScheduleController {
    private PimScheduleManager pimScheduleManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("pim-schedule-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        String userId = currentUserHolder.getUserId();
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = pimScheduleManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "pim/pim-schedule-list";
    }

    @RequestMapping("pim-schedule-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PimSchedule pimSchedule = pimScheduleManager.get(id);
            model.addAttribute("model", pimSchedule);
        }

        return "pim/pim-schedule-input";
    }

    @RequestMapping("pim-schedule-save")
    public String save(@ModelAttribute PimSchedule pimSchedule,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        PimSchedule dest = null;

        Long id = pimSchedule.getId();

        if (id != null) {
            dest = pimScheduleManager.get(id);
            beanMapper.copy(pimSchedule, dest);
        } else {
            dest = pimSchedule;

            String userId = currentUserHolder.getUserId();
            dest.setUserId(userId);

            String tenantId = tenantHolder.getTenantId();
            dest.setTenantId(tenantId);
        }

        pimScheduleManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/pim/pim-schedule-list.do";
    }

    @RequestMapping("pim-schedule-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PimSchedule> pimSchedules = pimScheduleManager
                .findByIds(selectedItem);

        pimScheduleManager.removeAll(pimSchedules);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/pim/pim-schedule-list.do";
    }

    @RequestMapping("pim-schedule-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = pimScheduleManager.pagedQuery(page, propertyFilters);

        List<PimSchedule> pimSchedules = (List<PimSchedule>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("pim schedule");
        tableModel.addHeaders("id", "name");
        tableModel.setData(pimSchedules);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("pim-schedule-view")
    public String view(Model model) {
        return "pim/pim-schedule-view";
    }

    // ~ ======================================================================
    @Resource
    public void setPimScheduleManager(PimScheduleManager pimScheduleManager) {
        this.pimScheduleManager = pimScheduleManager;
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
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
