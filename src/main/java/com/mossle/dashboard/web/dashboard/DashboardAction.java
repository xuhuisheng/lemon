package com.mossle.dashboard.web.dashboard;

import java.util.*;

import com.mossle.cms.domain.*;
import com.mossle.cms.manager.*;

import com.mossle.security.util.SpringSecurityUtils;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.*;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.*;
import org.activiti.engine.task.*;

public class DashboardAction {
    private ProcessEngine processEngine;
    private CmsArticleManager cmsArticleManager;
    private List<Task> personalTasks;
    private List<HistoricProcessInstance> historicProcessInstances;
    private List<ProcessDefinition> processDefinitions;
    private List<CmsArticle> cmsArticles;

    public String execute() {
        String currentUsername = SpringSecurityUtils.getCurrentUsername();
        personalTasks = processEngine.getTaskService().createTaskQuery()
                .taskAssignee(currentUsername).list();
        historicProcessInstances = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .startedBy(currentUsername).list();
        processDefinitions = processEngine.getRepositoryService()
                .createProcessDefinitionQuery().list();
        cmsArticles = cmsArticleManager.getAll();

        return "success";
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setCmsArticleManager(CmsArticleManager cmsArticleManager) {
        this.cmsArticleManager = cmsArticleManager;
    }

    public List<Task> getPersonalTasks() {
        return personalTasks;
    }

    public List<HistoricProcessInstance> getHistoricProcessInstances() {
        return historicProcessInstances;
    }

    public List<ProcessDefinition> getProcessDefinitions() {
        return processDefinitions;
    }

    public List<CmsArticle> getCmsArticles() {
        return cmsArticles;
    }
}
