package com.mossle.stamp.web;

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

import com.mossle.stamp.persistence.domain.StampTask;
import com.mossle.stamp.persistence.manager.StampTaskManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("stamp")
public class StampTaskController {
    private StampTaskManager stampTaskManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("stamp-task-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = stampTaskManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "stamp/stamp-task-list";
    }

    @RequestMapping("stamp-task-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            StampTask stampTask = stampTaskManager.get(id);
            model.addAttribute("model", stampTask);
        }

        return "stamp/stamp-task-input";
    }

    @RequestMapping("stamp-task-save")
    public String save(@ModelAttribute StampTask stampTask,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        StampTask dest = null;

        Long id = stampTask.getId();

        if (id != null) {
            dest = stampTaskManager.get(id);
            beanMapper.copy(stampTask, dest);
        } else {
            dest = stampTask;
            dest.setTenantId(tenantId);
        }

        stampTaskManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/stamp/stamp-task-list.do";
    }

    @RequestMapping("stamp-task-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<StampTask> stampTasks = stampTaskManager.findByIds(selectedItem);

        stampTaskManager.removeAll(stampTasks);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/stamp/stamp-task-list.do";
    }

    @RequestMapping("stamp-task-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = stampTaskManager.pagedQuery(page, propertyFilters);

        List<StampTask> stampTasks = (List<StampTask>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("stamp info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(stampTasks);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setStampTaskManager(StampTaskManager stampTaskManager) {
        this.stampTaskManager = stampTaskManager;
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
