package com.mossle.bpm.web.bpm;

import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.bpm.cmd.CounterSignCmd;
import com.mossle.bpm.cmd.HistoryProcessInstanceDiagramCmd;
import com.mossle.bpm.cmd.ProcessDefinitionDiagramCmd;
import com.mossle.bpm.cmd.RollbackTaskCmd;
import com.mossle.bpm.cmd.WithdrawTaskCmd;
import com.mossle.bpm.persistence.domain.BpmCategory;
import com.mossle.bpm.persistence.manager.BpmCategoryManager;

import com.mossle.core.struts2.BaseAction;

import com.mossle.security.util.SpringSecurityUtils;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.ServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

import org.apache.commons.io.IOUtils;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

/**
 * 我的流程 待办流程 已办未结
 */
@Results({ @Result(name = WorkspaceAction.RELOAD, location = "workspace!listProcessDefinitions.do?operationMode=RETRIEVE", type = "redirect") })
public class WorkspaceAction extends BaseAction {
    public static final String RELOAD = "reload";
    private ProcessEngine processEngine;
    private List<ProcessDefinition> processDefinitions;
    private String processDefinitionId;
    private StartFormData startFormData;
    private List<Task> tasks;
    private String taskId;
    private TaskFormData taskFormData;
    private String processInstanceId;
    private List<HistoricTaskInstance> historicTasks;
    private List<HistoricProcessInstance> historicProcessInstances;
    private List<HistoricVariableInstance> historicVariableInstances;
    private String username;
    private BpmCategoryManager bpmCategoryManager;
    private List<BpmCategory> bpmCategories;
    private String operationType;
    private String processDefinitionKey;
    private int processDefinitionVersion;

    public String home() {
        bpmCategories = bpmCategoryManager.getAll("priority", true);

        return "home";
    }

    public void graphProcessDefinition() throws Exception {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();

        if (processDefinitionId == null) {
            processDefinitionId = repositoryService
                    .createProcessDefinitionQuery()
                    .processDefinitionKey(processDefinitionKey)
                    .processDefinitionVersion(processDefinitionVersion)
                    .singleResult().getId();
        }

        Command<InputStream> cmd = null;
        cmd = new ProcessDefinitionDiagramCmd(processDefinitionId);

        InputStream is = processEngine.getManagementService().executeCommand(
                cmd);
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("image/png");

        IOUtils.copy(is, response.getOutputStream());
    }

    // ~ ======================================================================
    public String endProcessInstance() {
        processEngine.getRuntimeService().deleteProcessInstance(
                processInstanceId, "end");

        return RELOAD;
    }

    /**
     * 流程列表（所有的流程定义即流程模型）
     * 
     * @return
     */
    public String listProcessDefinitions() {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        processDefinitions = repositoryService.createProcessDefinitionQuery()
                .active().list();

        return "listProcessDefinitions";
    }

    public String listRunningProcessInstances() {
        HistoryService historyService = processEngine.getHistoryService();

        String currentUsername = SpringSecurityUtils.getCurrentUsername();
        historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery()
                .startedBy(currentUsername).unfinished().list();

        return "listRunningProcessInstances";
    }

    /**
     * 已结流程.
     * 
     * @return
     */
    public String listCompletedProcessInstances() {
        HistoryService historyService = processEngine.getHistoryService();

        String currentUsername = SpringSecurityUtils.getCurrentUsername();
        historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery()
                .startedBy(currentUsername).finished().list();

        return "listCompletedProcessInstances";
    }

    /**
     * 用户参与的流程（包含已经完成和未完成）
     * 
     * @return
     */
    public String listInvolvedProcessInstances() {
        HistoryService historyService = processEngine.getHistoryService();

        // TODO: finished(), unfinished()
        String currentUsername = SpringSecurityUtils.getCurrentUsername();
        historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery()
                .involvedUser(currentUsername).list();

        return "listInvolvedProcessInstances";
    }

    /**
     * 流程跟踪
     * 
     * @throws Exception
     */
    public void graphHistoryProcessInstance() throws Exception {
        Command<InputStream> cmd = new HistoryProcessInstanceDiagramCmd(
                processInstanceId);

        InputStream is = processEngine.getManagementService().executeCommand(
                cmd);
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("image/png");

        int len = 0;
        byte[] b = new byte[1024];

        while ((len = is.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    /**
     * 待办任务（个人任务）
     * 
     * @return
     */
    public String listPersonalTasks() {
        TaskService taskService = processEngine.getTaskService();
        String username = SpringSecurityUtils.getCurrentUsername();
        tasks = taskService.createTaskQuery().taskAssignee(username).active()
                .list();

        return "listPersonalTasks";
    }

    /**
     * 代领任务（组任务）
     * 
     * @return
     */
    public String listGroupTasks() {
        TaskService taskService = processEngine.getTaskService();
        String currentUsername = SpringSecurityUtils.getCurrentUsername();
        tasks = taskService.createTaskQuery()
                .taskCandidateUser(currentUsername).active().list();

        return "listGroupTasks";
    }

    /**
     * 代理中的任务（代理人还未完成该任务）
     * 
     * @return
     */
    public String listDelegatedTasks() {
        TaskService taskService = processEngine.getTaskService();
        String currentUsername = SpringSecurityUtils.getCurrentUsername();
        tasks = taskService.createTaskQuery().taskOwner(currentUsername)
                .taskDelegationState(DelegationState.PENDING).list();

        return "listDelegatedTasks";
    }

    /**
     * 已办任务（历史任务）
     * 
     * @return
     */
    public String listHistoryTasks() {
        HistoryService historyService = processEngine.getHistoryService();
        String currentUsername = SpringSecurityUtils.getCurrentUsername();
        historicTasks = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(currentUsername).finished().list();

        return "listHistoryTasks";
    }

    // ~ ======================================================================
    /**
     * 发起流程页面（启动一个流程实例）内置流程表单方式
     * 
     * @return
     */
    public String prepareStartProcessInstance() {
        FormService formService = processEngine.getFormService();
        startFormData = formService.getStartFormData(processDefinitionId);

        return "prepareStartProcessInstance";
    }

    /**
     * 发起流程
     * 
     * @return
     */
    public String startProcessInstance() {
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(SpringSecurityUtils
                .getCurrentUsername());

        FormService formService = processEngine.getFormService();
        startFormData = formService.getStartFormData(processDefinitionId);

        HttpServletRequest request = ServletActionContext.getRequest();
        Map<String, String> map = new HashMap<String, String>();

        for (FormProperty formProperty : startFormData.getFormProperties()) {
            String name = formProperty.getId();
            map.put(name, request.getParameter(name));
        }

        formService.submitStartFormData(processDefinitionId, map);

        return RELOAD;
    }

    // ~ ======================================================================
    /**
     * 完成任务页面
     * 
     * @return
     */
    public String prepareCompleteTask() {
        FormService formService = processEngine.getFormService();

        taskFormData = formService.getTaskFormData(taskId);

        return "prepareCompleteTask";
    }

    /**
     * 完成任务
     * 
     * @return
     */
    public String completeTask() {
        HttpServletRequest request = ServletActionContext.getRequest();
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(SpringSecurityUtils
                .getCurrentUsername());

        FormService formService = processEngine.getFormService();
        taskFormData = formService.getTaskFormData(taskId);

        Map<String, String> map = new HashMap<String, String>();

        for (FormProperty formProperty : taskFormData.getFormProperties()) {
            if (formProperty.isWritable()) {
                String name = formProperty.getId();
                map.put(name, request.getParameter(name));
            }
        }

        formService.submitTaskFormData(taskId, map);

        return RELOAD;
    }

    /**
     * 认领任务（对应的是在组任务，即从组任务中领取任务）
     * 
     * @return
     */
    public String claimTask() {
        String currentUsername = SpringSecurityUtils.getCurrentUsername();

        TaskService taskService = processEngine.getTaskService();
        taskService.claim(taskId, currentUsername);

        return RELOAD;
    }

    /**
     * 任务代理页面
     * 
     * @return
     */
    public String prepareDelegateTask() {
        return "prepareDelegateTask";
    }

    /**
     * 任务代理
     * 
     * @return
     */
    public String delegateTask() {
        TaskService taskService = processEngine.getTaskService();
        taskService.delegateTask(taskId, username);

        return RELOAD;
    }

    /**
     * TODO 该方法有用到？
     * 
     * @return
     */
    public String resolveTask() {
        TaskService taskService = processEngine.getTaskService();
        taskService.resolveTask(taskId);

        return RELOAD;
    }

    /**
     * 查看历史【包含流程跟踪、任务列表（完成和未完成）、流程变量】
     * 
     * @return
     */
    public String viewHistory() {
        HistoryService historyService = processEngine.getHistoryService();
        historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).list();
        historicVariableInstances = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).list();

        return "viewHistory";
    }

    // ~ ==================================国内特色流程====================================
    /**
     * 回退任务
     * 
     * @return
     */
    public String rollback() {
        Command<Integer> cmd = new RollbackTaskCmd(taskId);

        processEngine.getManagementService().executeCommand(cmd);

        return RELOAD;
    }

    /**
     * 取回任务
     * 
     * @return
     */
    public String withdraw() {
        Command<Integer> cmd = new WithdrawTaskCmd(taskId);

        processEngine.getManagementService().executeCommand(cmd);

        return RELOAD;
    }

    /**
     * 加减签
     */
    public String changeCounterSign() {
        return "changeCounterSign";
    }

    public String saveCounterSign() {
        CounterSignCmd cmd = new CounterSignCmd(operationType, username, taskId);

        processEngine.getManagementService().executeCommand(cmd);

        return RELOAD;
    }

    // ~ ======================================================================
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public List<ProcessDefinition> getProcessDefinitions() {
        return processDefinitions;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public StartFormData getStartFormData() {
        return startFormData;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public TaskFormData getTaskFormData() {
        return taskFormData;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public List<HistoricTaskInstance> getHistoricTasks() {
        return historicTasks;
    }

    public List<HistoricProcessInstance> getHistoricProcessInstances() {
        return historicProcessInstances;
    }

    public List<HistoricVariableInstance> getHistoricVariableInstances() {
        return historicVariableInstances;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBpmCategoryManager(BpmCategoryManager bpmCategoryManager) {
        this.bpmCategoryManager = bpmCategoryManager;
    }

    public List<BpmCategory> getBpmCategories() {
        return bpmCategories;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public void setProcessDefinitionVersion(int processDefinitionVersion) {
        this.processDefinitionVersion = processDefinitionVersion;
    }
}
