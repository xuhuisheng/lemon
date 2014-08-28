package com.mossle.bpm.component;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.process.ProcessConnector;

import com.mossle.core.page.Page;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

import org.springframework.stereotype.Component;

@Component
public class ActivitiProcessConnector implements ProcessConnector {
    private ProcessEngine processEngine;

    /**
     * 未结流程.
     */
    public Page findRunningProcessInstances(String userId, Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        // TODO: 改成通过runtime表搜索，提高效率
        long count = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId).unfinished().count();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery().startedBy(userId)
                .unfinished()
                .listPage((int) page.getStart(), page.getPageSize());

        page.setResult(historicProcessInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 已结流程.
     */
    public Page findCompletedProcessInstances(String userId, Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId).finished().count();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery().startedBy(userId)
                .finished().listPage((int) page.getStart(), page.getPageSize());

        page.setResult(historicProcessInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 参与流程.
     */
    public Page findInvolvedProcessInstances(String userId, Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        // TODO: finished(), unfinished()
        long count = historyService.createHistoricProcessInstanceQuery()
                .involvedUser(userId).count();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery().involvedUser(userId)
                .listPage((int) page.getStart(), page.getPageSize());

        page.setResult(historicProcessInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 待办任务（个人任务）.
     */
    public Page findPersonalTasks(String userId, Page page) {
        TaskService taskService = processEngine.getTaskService();

        long count = taskService.createTaskQuery().taskAssignee(userId)
                .active().count();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId)
                .active().listPage((int) page.getStart(), page.getPageSize());
        page.setResult(tasks);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 代领任务（组任务）.
     */
    public Page findGroupTasks(String userId, Page page) {
        TaskService taskService = processEngine.getTaskService();

        long count = taskService.createTaskQuery().taskCandidateUser(userId)
                .active().count();
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateUser(userId).active()
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(tasks);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 已办任务（历史任务）.
     */
    public Page findHistoryTasks(String userId, Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId).finished().count();
        List<HistoricTaskInstance> historicTaskInstances = historyService
                .createHistoricTaskInstanceQuery().taskAssignee(userId)
                .finished().listPage((int) page.getStart(), page.getPageSize());
        page.setResult(historicTaskInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 代理中的任务（代理人还未完成该任务）.
     */
    public Page findDelegatedTasks(String userId, Page page) {
        TaskService taskService = processEngine.getTaskService();

        long count = taskService.createTaskQuery().taskOwner(userId)
                .taskDelegationState(DelegationState.PENDING).count();
        List<Task> tasks = taskService.createTaskQuery().taskOwner(userId)
                .taskDelegationState(DelegationState.PENDING)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(tasks);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 流程定义.
     */
    public Page findProcessDefinitions(Page page) {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        long count = repositoryService.createProcessDefinitionQuery().count();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery().listPage((int) page.getStart(),
                        page.getPageSize());
        page.setResult(processDefinitions);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 流程实例.
     */
    public Page findProcessInstances(Page page) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        long count = runtimeService.createProcessInstanceQuery().count();
        List<ProcessInstance> processInstances = runtimeService
                .createProcessInstanceQuery().listPage((int) page.getStart(),
                        page.getPageSize());
        page.setResult(processInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 任务.
     */
    public Page findTasks(Page page) {
        TaskService taskService = processEngine.getTaskService();
        long count = taskService.createTaskQuery().count();
        List<Task> tasks = taskService.createTaskQuery().listPage(
                (int) page.getStart(), page.getPageSize());
        page.setResult(tasks);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 部署.
     */
    public Page findDeployments(Page page) {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        long count = repositoryService.createDeploymentQuery().count();
        List<Deployment> deployments = repositoryService
                .createDeploymentQuery().listPage((int) page.getStart(),
                        page.getPageSize());
        page.setResult(deployments);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 历史流程实例.
     */
    public Page findHistoricProcessInstances(Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricProcessInstanceQuery()
                .count();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery().listPage(
                        (int) page.getStart(), page.getPageSize());
        page.setResult(historicProcessInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 历史节点.
     */
    public Page findHistoricActivityInstances(Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricActivityInstanceQuery()
                .count();
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().listPage(
                        (int) page.getStart(), page.getPageSize());
        page.setResult(historicActivityInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 历史任务.
     */
    public Page findHistoricTaskInstances(Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricTaskInstanceQuery().count();
        List<HistoricTaskInstance> historicTaskInstances = historyService
                .createHistoricTaskInstanceQuery().listPage(
                        (int) page.getStart(), page.getPageSize());
        page.setResult(historicTaskInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 作业.
     */
    public Page findJobs(Page page) {
        ManagementService managementService = processEngine
                .getManagementService();

        long count = managementService.createJobQuery().count();
        List<Job> jobs = managementService.createJobQuery().listPage(
                (int) page.getStart(), page.getPageSize());
        page.setResult(jobs);
        page.setTotalCount(count);

        return page;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}
