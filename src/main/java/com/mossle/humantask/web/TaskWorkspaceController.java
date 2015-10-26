package com.mossle.humantask.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.MultipartHandler;
import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.MultipartFileDataSource;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.manager.TaskInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.jdbc.core.JdbcTemplate;

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
    private JdbcTemplate jdbcTemplate;

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
        String sql = "select ps.PARENT_ENTITY_ID as ID from PARTY_STRUCT ps,PARTY_ENTITY child,PARTY_TYPE type"
                + " where ps.CHILD_ENTITY_ID=child.ID and child.TYPE_ID=type.ID and type.TYPE='1' and child.REF=?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        List<String> partyIds = new ArrayList<String>();

        for (Map<String, Object> map : list) {
            partyIds.add(map.get("ID").toString());
        }
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("partyIds", partyIds);

        String hql = "from TaskInfo t join t.taskParticipants p with p.ref in (:partyIds)";
        page = taskInfoManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), map);
        // List<PropertyFilter> propertyFilters = PropertyFilter
        // .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_status", "active"));
        // propertyFilters.add(new PropertyFilter("INLS_assignee", null));
        // page = taskInfoManager.pagedQuery(page, propertyFilters);
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

    @RequestMapping("workspace-claimTask")
    public String claimTask(@RequestParam("taskId") Long taskId) {
        String userId = currentUserHolder.getUserId();
        TaskInfo taskInfo = taskInfoManager.get(taskId);
        taskInfo.setAssignee(userId);
        taskInfoManager.save(taskInfo);

        return "redirect:/humantask/workspace-personalTasks.do";
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

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
