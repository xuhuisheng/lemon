package com.mossle.bpm.web;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskConstants;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.keyvalue.KeyValueConnector;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.notification.NotificationConnector;
import com.mossle.api.notification.NotificationDTO;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.bpm.cmd.CounterSignCmd;
import com.mossle.bpm.cmd.FindHistoryGraphCmd;
import com.mossle.bpm.cmd.HistoryProcessInstanceDiagramCmd;
import com.mossle.bpm.cmd.ProcessDefinitionDiagramCmd;
import com.mossle.bpm.cmd.RollbackTaskCmd;
import com.mossle.bpm.graph.Graph;
import com.mossle.bpm.persistence.domain.BpmCategory;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmCategoryManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.service.TraceService;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;

import com.mossle.spi.process.InternalProcessConnector;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 我的流程 待办流程 已办未结
 */
@Controller
@RequestMapping("bpm")
public class WorkspaceController {
    private static Logger logger = LoggerFactory
            .getLogger(WorkspaceController.class);
    private BpmCategoryManager bpmCategoryManager;
    private BpmProcessManager bpmProcessManager;
    private ProcessEngine processEngine;
    private UserConnector userConnector;
    private ProcessConnector processConnector;
    private CurrentUserHolder currentUserHolder;
    private TraceService traceService;
    private TenantHolder tenantHolder;
    private KeyValueConnector keyValueConnector;
    private JsonMapper jsonMapper = new JsonMapper();
    private HumanTaskConnector humanTaskConnector;
    private NotificationConnector notificationConnector;
    private InternalProcessConnector internalProcessConnector;
    private String baseUrl;

    @RequestMapping("workspace-home")
    public String home(Model model) {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from BpmCategory where tenantId=? order by priority";
        List<BpmCategory> bpmCategories = bpmCategoryManager
                .find(hql, tenantId);
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
        Authentication.setAuthenticatedUserId(currentUserHolder.getUserId());
        processEngine.getRuntimeService().deleteProcessInstance(
                processInstanceId, "人工终止");

        return "redirect:/bpm/workspace-listRunningProcessInstances.do";
    }

    @RequestMapping("workspace-copyProcessInstance")
    public String copyProcessInstance(
            @RequestParam("processInstanceId") String processInstanceId)
            throws Exception {
        // 复制流程
        // 1. 从历史获取businessKey
        HistoricProcessInstance historicProcessInstance = processEngine
                .getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        String businessKey = historicProcessInstance.getBusinessKey();
        String processDefinitionId = historicProcessInstance
                .getProcessDefinitionId();

        // 2. 从businessKey获取keyvalue
        Record original = keyValueConnector.findByCode(businessKey);

        // 3. 找到流程的第一个form
        FormDTO formDto = this.processConnector
                .findStartForm(processDefinitionId);

        List<String> fieldNames = new ArrayList<String>();

        if (formDto.isExists()) {
            String content = formDto.getContent();
            logger.debug("content : {}", content);

            Map<String, Object> formJson = jsonMapper.fromJson(
                    formDto.getContent(), Map.class);
            List<Map<String, Object>> sections = (List<Map<String, Object>>) formJson
                    .get("sections");

            for (Map<String, Object> section : sections) {
                if (!"grid".equals(section.get("type"))) {
                    continue;
                }

                List<Map<String, Object>> fields = (List<Map<String, Object>>) section
                        .get("fields");

                for (Map<String, Object> field : fields) {
                    logger.debug("field : {}", field);

                    String type = (String) field.get("type");
                    String name = (String) field.get("name");
                    String label = name;

                    if ("label".equals(type)) {
                        continue;
                    }

                    // if (formField != null) {
                    // continue;
                    // }
                    fieldNames.add(name);
                }
            }
        }

        logger.debug("fieldNames : {}", fieldNames);

        // 4. 使用第一个form复制数据，后续的审批意见数据之类的不要复制
        Record record = keyValueConnector.copyRecord(original, fieldNames);

        // 5. 跳转到草稿箱
        return "redirect:/operation/process-operation-listDrafts.do";
    }

    /**
     * 流程列表（所有的流程定义即流程模型）
     * 
     * @return
     */
    @RequestMapping("workspace-listProcessDefinitions")
    public String listProcessDefinitions(Model model) {
        String tenantId = tenantHolder.getTenantId();
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionTenantId(tenantId).active().list();
        model.addAttribute("processDefinitions", processDefinitions);

        return "bpm/workspace-listProcessDefinitions";
    }

    @RequestMapping("workspace-listRunningProcessInstances")
    public String listRunningProcessInstances(@ModelAttribute Page page,
            Model model) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        page = processConnector.findRunningProcessInstances(userId, tenantId,
                page);
        model.addAttribute("page", page);

        return "bpm/workspace-listRunningProcessInstances";
    }

    /**
     * 已结流程.
     * 
     * @return
     */
    @RequestMapping("workspace-listCompletedProcessInstances")
    public String listCompletedProcessInstances(@ModelAttribute Page page,
            Model model) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        page = processConnector.findCompletedProcessInstances(userId, tenantId,
                page);
        model.addAttribute("page", page);

        return "bpm/workspace-listCompletedProcessInstances";
    }

    /**
     * 用户参与的流程（包含已经完成和未完成）
     * 
     * @return
     */
    @RequestMapping("workspace-listInvolvedProcessInstances")
    public String listInvolvedProcessInstances(@ModelAttribute Page page,
            Model model) {
        // TODO: finished(), unfinished()
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findInvolvedProcessInstances(userId, tenantId,
                page);
        model.addAttribute("page", page);

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
    public String listPersonalTasks(@ModelAttribute Page page, Model model) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findPersonalTasks(userId, tenantId, page);
        model.addAttribute("page", page);

        return "bpm/workspace-listPersonalTasks";
    }

    /**
     * 代领任务（组任务）
     * 
     * @return
     */
    @RequestMapping("workspace-listGroupTasks")
    public String listGroupTasks(@ModelAttribute Page page, Model model) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        page = processConnector.findGroupTasks(userId, tenantId, page);
        model.addAttribute("page", page);

        return "bpm/workspace-listGroupTasks";
    }

    /**
     * 已办任务（历史任务）
     * 
     * @return
     */
    @RequestMapping("workspace-listHistoryTasks")
    public String listHistoryTasks(@ModelAttribute Page page, Model model) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        page = processConnector.findHistoryTasks(userId, tenantId, page);
        model.addAttribute("page", page);

        return "bpm/workspace-listHistoryTasks";
    }

    /**
     * 代理中的任务（代理人还未完成该任务）
     * 
     * @return
     */
    @RequestMapping("workspace-listDelegatedTasks")
    public String listDelegatedTasks(@ModelAttribute Page page, Model model) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findGroupTasks(userId, tenantId, page);
        model.addAttribute("page", page);

        return "bpm/workspace-listDelegatedTasks";
    }

    /**
     * 同时返回已领取和未领取的任务.
     */
    @RequestMapping("workspace-listCandidateOrAssignedTasks")
    public String listCandidateOrAssignedTasks(@ModelAttribute Page page,
            Model model) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findCandidateOrAssignedTasks(userId, tenantId,
                page);
        model.addAttribute("page", page);

        return "bpm/workspace-listCandidateOrAssignedTasks";
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
        String userId = currentUserHolder.getUserId();

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
     * 查看历史【包含流程跟踪、任务列表（完成和未完成）、流程变量】.
     */
    @RequestMapping("workspace-viewHistory")
    public String viewHistory(
            @RequestParam("processInstanceId") String processInstanceId,
            Model model) {
        String userId = currentUserHolder.getUserId();
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        if (userId.equals(historicProcessInstance.getStartUserId())) {
            // startForm
        }

        List<HistoricTaskInstance> historicTasks = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).list();
        // List<HistoricVariableInstance> historicVariableInstances = historyService
        // .createHistoricVariableInstanceQuery()
        // .processInstanceId(processInstanceId).list();
        model.addAttribute("historicTasks", historicTasks);

        // 获取流程对应的所有人工任务（目前还没有区分历史）
        List<HumanTaskDTO> humanTasks = humanTaskConnector
                .findHumanTasksByProcessInstanceId(processInstanceId);
        List<HumanTaskDTO> humanTaskDtos = new ArrayList<HumanTaskDTO>();

        for (HumanTaskDTO humanTaskDto : humanTasks) {
            if (humanTaskDto.getParentId() != null) {
                continue;
            }

            humanTaskDtos.add(humanTaskDto);
        }

        model.addAttribute("humanTasks", humanTaskDtos);
        // model.addAttribute("historicVariableInstances",
        // historicVariableInstances);
        model.addAttribute("nodeDtos",
                traceService.traceProcessInstance(processInstanceId));
        model.addAttribute("historyActivities", processEngine
                .getHistoryService().createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).list());

        if (historicProcessInstance.getEndTime() == null) {
            model.addAttribute("currentActivities", processEngine
                    .getRuntimeService()
                    .getActiveActivityIds(processInstanceId));
        } else {
            model.addAttribute("currentActivities", Collections
                    .singletonList(historicProcessInstance.getEndActivityId()));
        }

        Graph graph = processEngine.getManagementService().executeCommand(
                new FindHistoryGraphCmd(processInstanceId));
        model.addAttribute("graph", graph);
        model.addAttribute("historicProcessInstance", historicProcessInstance);

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
        Command<Object> cmd = new RollbackTaskCmd(taskId, null);

        processEngine.getManagementService().executeCommand(cmd);

        return "redirect:/bpm/workspace-listPersonalTasks.do";
    }

    /**
     * 撤销任务
     * 
     * @return
     */

    /*
     * @RequestMapping("workspace-withdraw") public String withdraw(@RequestParam("taskId") String taskId) {
     * Command<Integer> cmd = new WithdrawTaskCmd(taskId);
     * 
     * processEngine.getManagementService().executeCommand(cmd);
     * 
     * return "redirect:/bpm/workspace-listPersonalTasks.do"; }
     */

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
     * 转发已结流程.
     */
    @RequestMapping("workspace-transferProcessInstance")
    public String transferProcessInstance(
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam("assignee") String assignee) {
        String tenantId = tenantHolder.getTenantId();

        // 1. 找到历史
        HistoricProcessInstance historicProcessInstance = processEngine
                .getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();

        // 2. 创建一个任务，设置为未读，转发状态
        HumanTaskDTO humanTaskDto = humanTaskConnector.createHumanTask();
        humanTaskDto.setProcessInstanceId(processInstanceId);
        humanTaskDto.setPresentationSubject(historicProcessInstance.getName());
        humanTaskDto.setAssignee(assignee);
        humanTaskDto.setTenantId(tenantId);
        // TODO: 还没有字段
        // humanTaskDto.setCopyStatus("unread");
        humanTaskDto.setCatalog(HumanTaskConstants.CATALOG_COPY);
        humanTaskDto.setAction("unread");
        humanTaskDto.setBusinessKey(historicProcessInstance.getBusinessKey());
        humanTaskDto.setProcessDefinitionId(historicProcessInstance
                .getProcessDefinitionId());

        try {
            // TODO: 等到流程支持viewFormKey，才能设置。目前做不到
            List<HistoricTaskInstance> historicTaskInstances = processEngine
                    .getHistoryService().createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId).list();
            HistoricTaskInstance historicTaskInstance = historicTaskInstances
                    .get(0);
            humanTaskDto.setForm(historicTaskInstance.getFormKey());
            humanTaskDto.setName(historicTaskInstance.getName());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        humanTaskConnector.saveHumanTask(humanTaskDto);

        // 3. 把任务分配给对应的人员
        return "redirect:/bpm/workspace-listCompletedProcessInstances.do";
    }

    /**
     * 催办.
     */
    @RequestMapping("workspace-remind")
    public String remind(
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam("userId") String userId,
            @RequestParam("comment") String comment) {
        List<HumanTaskDTO> humanTaskDtos = humanTaskConnector
                .findHumanTasksByProcessInstanceId(processInstanceId);
        logger.debug("processInstanceId : {}", processInstanceId);

        logger.debug("humanTaskDtos : {}", humanTaskDtos);

        for (HumanTaskDTO humanTaskDto : humanTaskDtos) {
            if (humanTaskDto.getCompleteTime() != null) {
                continue;
            }

            String assignee = humanTaskDto.getAssignee();
            logger.debug("remind {}", assignee);

            NotificationDTO notificationDto = new NotificationDTO();
            notificationDto.setSender(currentUserHolder.getUserId());
            notificationDto.setReceiver(assignee);
            notificationDto.setReceiverType("userid");
            notificationDto.getTypes().add("msg");
            notificationDto.getTypes().add("email");
            notificationDto.setSubject("请尽快办理 "
                    + humanTaskDto.getPresentationSubject());

            String url = baseUrl
                    + "/operation/task-operation-viewTaskForm.do?humanTaskId="
                    + humanTaskDto.getId();
            String content = "请尽快办理 " + humanTaskDto.getPresentationSubject()
                    + "<p><a href='" + url + "'>" + url + "</a></p>";
            notificationDto.setContent(content);

            notificationConnector.send(notificationDto, "1");
        }

        return "redirect:/bpm/workspace-listRunningProcessInstances.do";
    }

    /**
     * 跳过.
     */
    @RequestMapping("workspace-skip")
    public String skip(
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam("userId") String userId,
            @RequestParam("comment") String comment) {
        List<HumanTaskDTO> humanTaskDtos = humanTaskConnector
                .findHumanTasksByProcessInstanceId(processInstanceId);
        logger.debug("processInstanceId : {}", processInstanceId);

        logger.debug("humanTaskDtos : {}", humanTaskDtos);

        for (HumanTaskDTO humanTaskDto : humanTaskDtos) {
            if (humanTaskDto.getCompleteTime() != null) {
                continue;
            }

            String humanTaskId = humanTaskDto.getId();
            humanTaskConnector.skip(humanTaskId, currentUserHolder.getUserId(),
                    comment);
        }

        return "redirect:/bpm/workspace-listRunningProcessInstances.do";
    }

    /**
     * 撤销.
     */
    @RequestMapping("workspace-withdraw")
    public String withdraw(
            @RequestParam("processInstanceId") String processInstanceId) {
        logger.debug("processInstanceId : {}", processInstanceId);

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        String initiator = "";
        String firstUserTaskActivityId = internalProcessConnector
                .findFirstUserTaskActivityId(
                        processInstance.getProcessDefinitionId(), initiator);
        logger.debug("firstUserTaskActivityId : {}", firstUserTaskActivityId);

        List<HistoricTaskInstance> historicTaskInstances = processEngine
                .getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(firstUserTaskActivityId).list();
        HistoricTaskInstance historicTaskInstance = historicTaskInstances
                .get(0);
        String taskId = historicTaskInstance.getId();
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTaskByTaskId(taskId);
        String comment = "";
        humanTaskConnector.withdraw(humanTaskDto.getId(), comment);

        return "redirect:/bpm/workspace-listRunningProcessInstances.do";
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

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTraceService(TraceService traceService) {
        this.traceService = traceService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setKeyValueConnector(KeyValueConnector keyValueConnector) {
        this.keyValueConnector = keyValueConnector;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }

    @Resource
    public void setNotificationConnector(
            NotificationConnector notificationConnector) {
        this.notificationConnector = notificationConnector;
    }

    @Resource
    public void setInternalProcessConnector(
            InternalProcessConnector internalProcessConnector) {
        this.internalProcessConnector = internalProcessConnector;
    }

    @Value("${application.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
