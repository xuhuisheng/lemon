package com.mossle.humantask.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.MultipartHandler;
import com.mossle.ext.auth.CurrentUserHolder;
import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.store.MultipartFileDataSource;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.manager.TaskInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.util.MultiValueMap;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("humantask")
public class TaskWorkspaceController {
    private static Logger logger = LoggerFactory
            .getLogger(TaskWorkspaceController.class);
    private TaskInfoManager taskInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("workspace-personalTasks")
    public String personalTasks(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_status", "active"));
        propertyFilters.add(new PropertyFilter("EQS_assignee", userId));
        page = taskInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "humantask/workspace-personalTasks";
    }

    @RequestMapping("workspace-groupTasks")
    public String groupTasks(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_status", "active"));
        propertyFilters.add(new PropertyFilter("INLS_assignee", null));
        page = taskInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "humantask/workspace-groupTasks";
    }

    @RequestMapping("workspace-historyTasks")
    public String historyTasks(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_status", "complete"));
        propertyFilters.add(new PropertyFilter("EQS_assignee", userId));
        page = taskInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "humantask/workspace-historyTasks";
    }

    @RequestMapping("workspace-delegatedTasks")
    public String delegatedTasks(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_status", "active"));
        propertyFilters.add(new PropertyFilter("EQS_owner", userId));
        page = taskInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "humantask/workspace-delegatedTasks";
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
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
