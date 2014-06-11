package com.mossle.bpm.web;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.bpm.cmd.CounterSignCmd;
import com.mossle.bpm.cmd.DelegateTaskCmd;
import com.mossle.bpm.cmd.HistoryProcessInstanceDiagramCmd;
import com.mossle.bpm.cmd.ProcessDefinitionDiagramCmd;
import com.mossle.bpm.cmd.RollbackTaskCmd;
import com.mossle.bpm.cmd.WithdrawTaskCmd;
import com.mossle.bpm.persistence.domain.BpmCategory;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmCategoryManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

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
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

import org.apache.commons.io.IOUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 我的流程 待办流程 已办未结
 */
@Controller
@RequestMapping("bpm")
public class WorkspaceController {
    private BpmCategoryManager bpmCategoryManager;
    private BpmProcessManager bpmProcessManager;
    private ProcessEngine processEngine;
    private UserConnector userConnector;

    @RequestMapping("workspace-home")
    public String home(Model model) {
        List<BpmCategory> bpmCategories = bpmCategoryManager.getAll("priority",
                true);
        model.addAttribute("bpmCategories", bpmCategories);

        return "bpm/workspace-home";
    }

    @RequestMapping("workspace-graphProcessDefinition")
    public void graphProcessDefinition(
            @RequestParam("bpmProcessId") Long bpmProcessId,
            HttpServletResponse response) throws Exception {
        BpmProcess bpmProcess = bpmProcessManager.get(bpmProcessId);
        String processDefinitionId = bpmProcess.getBpmConfBase()
                .getProcessDefinitionId();

        Command<InputStream> cmd = null;
        cmd = new ProcessDefinitionDiagramCmd(processDefinitionId);

        InputStream is = processEngine.getManagementService().executeCommand(
                cmd);
        response.setContentType("image/png");

        IOUtils.copy(is, response.getOutputStream());
    }

    // ~ ======================================================================
    @RequestMapping("workspace-endProcessInstance")
    public String endProcessInstance(
            @RequestParam("processInstanceId") String processInstanceId) {
        processEngine.getRuntimeService().deleteProcessInstance(
                processInstanceId, "end");

        return "redirect:/bpm/workspace-listProcessInstances.do";
    }

    /**
     * 流程列表（所有的流程定义即流程模型）
     * 
     * @return
     */
    @RequestMapping("workspace-listProcessDefinitions")
    public String listProcessDefinitions(Model model) {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery().active().list();
        model.addAttribute("processDefinitions", processDefinitions);

        return "bpm/workspace-listProcessDefinitions";
    }

    @RequestMapping("workspace-listRunningProcessInstances")
    public String listRunningProcessInstances(Model model) {
        HistoryService historyService = processEngine.getHistoryService();

        // TODO: 改成通过runtime表搜索，提高效率
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery().startedBy(userId)
                .unfinished().list();
        model.addAttribute("historicProcessInstances", historicProcessInstances);

        return "bpm/workspace-listRunningProcessInstances";
    }

    /**
     * 已结流程.
     * 
     * @return
     */
    @RequestMapping("workspace-listCompletedProcessInstances")
    public String listCompletedProcessInstances(Model model) {
        HistoryService historyService = processEngine.getHistoryService();

        String userId = SpringSecurityUtils.getCurrentUserId();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery().startedBy(userId)
                .finished().list();
        model.addAttribute("historicProcessInstances", historicProcessInstances);

        return "bpm/workspace-listCompletedProcessInstances";
    }

    /**
     * 用户参与的流程（包含已经完成和未完成）
     * 
     * @return
     */
    @RequestMapping("workspace-listInvolvedProcessInstances")
    public String listInvolvedProcessInstances(Model model) {
        HistoryService historyService = processEngine.getHistoryService();

        // TODO: finished(), unfinished()
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery().involvedUser(userId)
                .list();
        model.addAttribute("historicProcessInstances", historicProcessInstances);

        return "bpm/workspace-listInvolvedProcessInstances";
    }

    /**
     * 流程跟踪
     * 
     * @throws Exception
     */
    @RequestMapping("workspace-graphHistoryProcessInstance")
    public void graphHistoryProcessInstance(
            @RequestParam("processInstanceId") String processInstanceId,
            HttpServletResponse response) throws Exception {
        Command<InputStream> cmd = new HistoryProcessInstanceDiagramCmd(
                processInstanceId);

        InputStream is = processEngine.getManagementService().executeCommand(
                cmd);
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
    @RequestMapping("workspace-listPersonalTasks")
    public String listPersonalTasks(Model model) {
        TaskService taskService = processEngine.getTaskService();
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId)
                .active().list();
        model.addAttribute("tasks", tasks);

        return "bpm/workspace-listPersonalTasks";
    }

    /**
     * 代领任务（组任务）
     * 
     * @return
     */
    @RequestMapping("workspace-listGroupTasks")
    public String listGroupTasks(Model model) {
        TaskService taskService = processEngine.getTaskService();
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateUser(userId).active().list();
        model.addAttribute("tasks", tasks);

        return "bpm/workspace-listGroupTasks";
    }

    /**
     * 代理中的任务（代理人还未完成该任务）
     * 
     * @return
     */
    @RequestMapping("workspace-listDelegatedTasks")
    public String listDelegatedTasks(Model model) {
        TaskService taskService = processEngine.getTaskService();
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<Task> tasks = taskService.createTaskQuery().taskOwner(userId)
                .taskDelegationState(DelegationState.PENDING).list();

        model.addAttribute("tasks", tasks);

        return "bpm/workspace-listDelegatedTasks";
    }

    /**
     * 已办任务（历史任务）
     * 
     * @return
     */
    @RequestMapping("workspace-listHistoryTasks")
    public String listHistoryTasks(Model model) {
        HistoryService historyService = processEngine.getHistoryService();
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<HistoricTaskInstance> historicTasks = historyService
                .createHistoricTaskInstanceQuery().taskAssignee(userId)
                .finished().list();
        model.addAttribute("historicTasks", historicTasks);

        return "bpm/workspace-listHistoryTasks";
    }

    // ~ ======================================================================
    /**
     * 发起流程页面（启动一个流程实例）内置流程表单方式
     * 
     * @return
     */
    @RequestMapping("workspace-prepareStartProcessInstance")
    public String prepareStartProcessInstance(
            @RequestParam("processDefinitionId") String processDefinitionId,
            Model model) {
        FormService formService = processEngine.getFormService();
        StartFormData startFormData = formService
                .getStartFormData(processDefinitionId);
        model.addAttribute("startFormData", startFormData);

        return "bpm/workspace prepareStartProcessInstance";
    }

    // ~ ======================================================================
    /**
     * 完成任务页面
     * 
     * @return
     */
    @RequestMapping("workspace-prepareCompleteTask")
    public String prepareCompleteTask(@RequestParam("taskId") String taskId,
            Model model) {
        FormService formService = processEngine.getFormService();

        TaskFormData taskFormData = formService.getTaskFormData(taskId);

        model.addAttribute("taskFormData", taskFormData);

        return "bpm/workspace-prepareCompleteTask";
    }

    /**
     * 认领任务（对应的是在组任务，即从组任务中领取任务）
     * 
     * @return
     */
    @RequestMapping("workspace-claimTask")
    public String claimTask(@RequestParam("taskId") String taskId) {
        String userId = SpringSecurityUtils.getCurrentUserId();

        TaskService taskService = processEngine.getTaskService();
        taskService.claim(taskId, userId);

        return "redirect:/bpm/workspace-listPersonalTasks.do";
    }

    /**
     * 任务代理页面
     * 
     * @return
     */
    @RequestMapping("workspace-prepareDelegateTask")
    public String prepareDelegateTask() {
        return "bpm/workspace-prepareDelegateTask";
    }

    /**
     * 任务代理
     * 
     * @return
     */
    @RequestMapping("workspace-delegateTask")
    public String delegateTask(@RequestParam("taskId") String taskId,
            @RequestParam("userId") String userId) {
        TaskService taskService = processEngine.getTaskService();
        taskService.delegateTask(taskId, userId);

        return "redirect:/bpm/workspace-listPersonalTasks.do";
    }

    /**
     * TODO 该方法有用到？
     * 
     * @return
     */
    @RequestMapping("workspace-resolveTask")
    public String resolveTask(@RequestParam("taskId") String taskId) {
        TaskService taskService = processEngine.getTaskService();
        taskService.resolveTask(taskId);

        return "redirect:/bpm/workspace-listPersonalTasks.do";
    }

    /**
     * 查看历史【包含流程跟踪、任务列表（完成和未完成）、流程变量】
     * 
     * @return
     */
    @RequestMapping("workspace-viewHistory")
    public String viewHistory(
            @RequestParam("processInstanceId") String processInstanceId,
            Model model) {
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricTaskInstance> historicTasks = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).list();
        List<HistoricVariableInstance> historicVariableInstances = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).list();
        model.addAttribute("historicTasks", historicTasks);
        model.addAttribute("historicVariableInstances",
                historicVariableInstances);

        return "bpm/workspace-viewHistory";
    }

    // ~ ==================================国内特色流程====================================
    /**
     * 回退任务
     * 
     * @return
     */
    @RequestMapping("workspace-rollback")
    public String rollback(@RequestParam("taskId") String taskId) {
        Command<Integer> cmd = new RollbackTaskCmd(taskId);

        processEngine.getManagementService().executeCommand(cmd);

        return "redirect:/bpm/workspace-listPersonalTasks.do";
    }

    /**
     * 取回任务
     * 
     * @return
     */
    @RequestMapping("workspace-withdraw")
    public String withdraw(@RequestParam("taskId") String taskId) {
        Command<Integer> cmd = new WithdrawTaskCmd(taskId);

        processEngine.getManagementService().executeCommand(cmd);

        return "redirect:/bpm/workspace-listPersonalTasks.do";
    }

    /**
     * 准备加减签.
     */
    @RequestMapping("workspace-changeCounterSign")
    public String changeCounterSign() {
        return "bpm/workspace-changeCounterSign";
    }

    /**
     * 进行加减签.
     */
    @RequestMapping("workspace-saveCounterSign")
    public String saveCounterSign(
            @RequestParam("operationType") String operationType,
            @RequestParam("userId") String userId,
            @RequestParam("taskId") String taskId) {
        CounterSignCmd cmd = new CounterSignCmd(operationType, userId, taskId);

        processEngine.getManagementService().executeCommand(cmd);

        return "redirect:/bpm/workspace-listPersonalTasks.do";
    }

    /**
     * 转办.
     */
    @RequestMapping("workspace-doDelegate")
    public String doDelegate(@RequestParam("taskId") String taskId,
            @RequestParam("attorney") String attorney) {
        DelegateTaskCmd cmd = new DelegateTaskCmd(taskId, attorney);
        processEngine.getManagementService().executeCommand(cmd);

        return "redirect:/bpm/workspace-listPersonalTasks.do";
    }

    /**
     * 协办.
     */
    @RequestMapping("workspace-doDelegateHelp")
    public String doDelegateHelp(@RequestParam("taskId") String taskId,
            @RequestParam("attorney") String attorney) {
        TaskService taskService = processEngine.getTaskService();
        taskService.delegateTask(taskId, attorney);

        return "redirect:/bpm/workspace-listPersonalTasks.do";
    }

    // ~ ======================================================================
    @Resource
    public void setBpmCategoryManager(BpmCategoryManager bpmCategoryManager) {
        this.bpmCategoryManager = bpmCategoryManager;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
