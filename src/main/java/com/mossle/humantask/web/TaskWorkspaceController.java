package com.mossle.humantask.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.manager.TaskInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * 待办任务.
     */
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

    /**
     * 待领任务.
     */
    @RequestMapping("workspace-groupTasks")
    public String groupTasks(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<String> partyIds = new ArrayList<String>();
        partyIds.addAll(this.findGroupIds(userId));
        partyIds.addAll(this.findUserIds(userId));

        logger.debug("party ids : {}", partyIds);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("partyIds", partyIds);

        String hql = "select t from TaskInfo t join t.taskParticipants p with p.ref in (:partyIds)";
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

    public List<String> findGroupIds(String userId) {
        String groupSql = "select ps.PARENT_ENTITY_ID as ID from PARTY_STRUCT ps,PARTY_ENTITY child,PARTY_TYPE type"
                + " where ps.CHILD_ENTITY_ID=child.ID and child.TYPE_ID=type.ID and type.TYPE='1' and child.REF=?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(groupSql,
                userId);
        List<String> partyIds = new ArrayList<String>();

        for (Map<String, Object> map : list) {
            partyIds.add(map.get("ID").toString());
        }

        return partyIds;
    }

    public List<String> findUserIds(String userId) {
        String userSql = "select pe.ID as ID from PARTY_ENTITY pe,PARTY_TYPE type"
                + " where pe.TYPE_ID=type.ID and type.TYPE='1' and pe.REF=?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(userSql,
                userId);
        List<String> partyIds = new ArrayList<String>();

        for (Map<String, Object> map : list) {
            partyIds.add(map.get("ID").toString());
        }

        return partyIds;
    }

    /**
     * 已办任务.
     */
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

    /**
     * 代理中的任务.
     */
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
