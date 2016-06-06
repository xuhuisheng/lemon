package com.mossle.humantask.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.manager.TaskInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("humantask")
public class TaskInfoController {
    private static Logger logger = LoggerFactory
            .getLogger(TaskInfoController.class);
    private TaskInfoManager taskInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("task-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = taskInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "humantask/task-info-list";
    }

    @RequestMapping("task-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            TaskInfo taskInfo = taskInfoManager.get(id);
            model.addAttribute("model", taskInfo);
        }

        return "humantask/task-info-input";
    }

    @RequestMapping("task-info-save")
    public String save(@ModelAttribute TaskInfo taskInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        TaskInfo dest = null;
        Long id = taskInfo.getId();

        if (id != null) {
            dest = taskInfoManager.get(id);
            beanMapper.copy(taskInfo, dest);
        } else {
            dest = taskInfo;
            dest.setCreator(userId);
            dest.setInitiator(userId);
            dest.setTenantId(tenantId);
            dest.setCreateTime(new Date());
        }

        taskInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/humantask/task-info-list.do";
    }

    @RequestMapping("task-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<TaskInfo> taskInfos = taskInfoManager.findByIds(selectedItem);

        taskInfoManager.removeAll(taskInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/humantask/task-info-list.do";
    }

    @RequestMapping("task-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = taskInfoManager.pagedQuery(page, propertyFilters);

        List<TaskInfo> dynamicModels = (List<TaskInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("dynamic model");
        tableModel.addHeaders("id", "name");
        tableModel.setData(dynamicModels);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setTaskInfoManager(TaskInfoManager taskInfoManager) {
        this.taskInfoManager = taskInfoManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
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
}
