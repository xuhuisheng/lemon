package com.mossle.bpm.support;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;

import com.mossle.bpm.cmd.FindFirstTaskFormCmd;
import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.client.user.UserClient;

import com.mossle.core.page.Page;

import com.mossle.spi.humantask.TaskDefinitionConnector;
import com.mossle.spi.humantask.TaskUserDTO;
import com.mossle.spi.process.FirstTaskForm;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessConnectorImpl implements ProcessConnector {
    private Logger logger = LoggerFactory.getLogger(ProcessConnectorImpl.class);
    private ProcessEngine processEngine;
    private BpmConfFormManager bpmConfFormManager;
    private BpmProcessManager bpmProcessManager;
    private BpmConfBaseManager bpmConfBaseManager;
    private UserClient userClient;
    private FormConnector formConnector;
    private TaskDefinitionConnector taskDefinitionConnector;

    public String startProcess(String userId, String businessKey,
            String processDefinitionId, Map<String, Object> processParameters) {
        // 先设置登录用户
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(userId);
        processParameters.put("_starter", userId);
        logger.info("businessKey : {}", businessKey);

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceById(processDefinitionId, businessKey,
                        processParameters);

        /*
         * // {流程标题:title}-{发起人:startUser}-{发起时间:startTime} String processDefinitionName =
         * processEngine.getRepositoryService() .createProcessDefinitionQuery()
         * .processDefinitionId(processDefinitionId).singleResult() .getName(); String processInstanceName =
         * processDefinitionName + "-" + userConnector.findById(userId).getDisplayName() + "-" + new
         * SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
         * processEngine.getRuntimeService().setProcessInstanceName( processInstance.getId(), processInstanceName);
         */
        return processInstance.getId();
    }

    public ProcessDTO findProcess(String processId) {
        if (processId == null) {
            logger.info("processId is null");

            return null;
        }

        BpmProcess bpmProcess = bpmProcessManager
                .get(Long.parseLong(processId));

        return this.convertProcess(bpmProcess);
    }

    public ProcessDTO findProcessByProcessDefinitionId(
            String processDefinitionId) {
        BpmConfBase bpmConfBase = bpmConfBaseManager.findUniqueBy(
                "processDefinitionId", processDefinitionId);
        String hql = "select bpmProcess from BpmProcess bpmProcess where bpmProcess.bpmConfBase=?";
        BpmProcess bpmProcess = bpmProcessManager.findUnique(hql, bpmConfBase);

        return this.convertProcess(bpmProcess);
    }

    public ProcessDTO convertProcess(BpmProcess bpmProcess) {
        ProcessDTO processDto = new ProcessDTO();
        BpmConfBase bpmConfBase = bpmProcess.getBpmConfBase();

        if (bpmConfBase == null) {
            logger.info("cannot find bpmConfBase : {}", bpmProcess.getId());

            return processDto;
        }

        String processDefinitionId = bpmConfBase.getProcessDefinitionId();
        ProcessDefinition processDefinition = processEngine
                .getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        String processDefinitionName = bpmProcess.getName();
        processDto.setId(Long.toString(bpmProcess.getId()));
        processDto.setProcessDefinitionId(processDefinitionId);
        processDto.setProcessDefinitionName(processDefinitionName);
        processDto.setCategory(processDefinition.getCategory());
        processDto.setKey(processDefinition.getKey());
        processDto.setVersion(processDefinition.getVersion());
        processDto.setConfigTask(Integer.valueOf(1).equals(
                bpmProcess.getUseTaskConf()));

        return processDto;
    }

    /**
     * 获得启动表单.
     */
    public FormDTO findStartForm(String processDefinitionId) {
        ProcessDefinition processDefinition = processEngine
                .getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        FirstTaskForm firstTaskForm = processEngine.getManagementService()
                .executeCommand(new FindFirstTaskFormCmd(processDefinitionId));

        if ((!firstTaskForm.isExists())
                && (firstTaskForm.getActivityId() != null)) {
            // 再从数据库里找一遍配置
            com.mossle.spi.humantask.FormDTO humantaskFormDto = taskDefinitionConnector
                    .findForm(firstTaskForm.getActivityId(),
                            processDefinitionId);

            if (humantaskFormDto != null) {
                firstTaskForm.setFormKey(humantaskFormDto.getKey());
            }
        }

        if (!firstTaskForm.isExists()) {
            logger.info("cannot find startForm : {}", processDefinitionId);

            return new FormDTO();
        }

        if (!firstTaskForm.isTaskForm()) {
            logger.info("find startEventForm : {}", processDefinitionId);

            return this.findStartEventForm(firstTaskForm);
        }

        List<TaskUserDTO> taskUserDtos = taskDefinitionConnector.findTaskUsers(
                firstTaskForm.getActivityId(),
                firstTaskForm.getProcessDefinitionId());
        String assignee = firstTaskForm.getAssignee();
        logger.debug("assignee : {}", assignee);

        for (TaskUserDTO taskUserDto : taskUserDtos) {
            logger.debug("catalog : {}, user : {}", taskUserDto.getCatalog(),
                    taskUserDto.getValue());

            if ("assignee".equals(taskUserDto.getCatalog())) {
                assignee = taskUserDto.getValue();

                break;
            }
        }

        logger.debug("assignee : {}", assignee);

        boolean exists = assignee != null;

        if ((("${" + firstTaskForm.getInitiatorName() + "}").equals(assignee))
                || "常用语:流程发起人".equals(assignee)
                || ((assignee != null) && assignee.equals(Authentication
                        .getAuthenticatedUserId()))) {
            exists = true;
        } else {
            exists = false;
        }

        if (!exists) {
            logger.info("cannot find taskForm : {}, {}", processDefinitionId,
                    firstTaskForm.getActivityId());

            return new FormDTO();
        }

        com.mossle.spi.humantask.FormDTO taskFormDto = taskDefinitionConnector
                .findForm(firstTaskForm.getActivityId(),
                        firstTaskForm.getProcessDefinitionId());

        List<BpmConfForm> bpmConfForms = bpmConfFormManager
                .find("from BpmConfForm where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        firstTaskForm.getProcessDefinitionId(),
                        firstTaskForm.getActivityId());

        if (taskFormDto == null) {
            logger.info("cannot find bpmConfForm : {}, {}",
                    processDefinitionId, firstTaskForm.getActivityId());

            return new FormDTO();
        }

        FormDTO formDto = new FormDTO();
        formDto.setProcessDefinitionId(firstTaskForm.getProcessDefinitionId());
        formDto.setActivityId(firstTaskForm.getActivityId());

        FormDTO contentFormDto = formConnector.findForm(taskFormDto.getKey(),
                processDefinition.getTenantId());

        if (contentFormDto == null) {
            logger.error("cannot find form : {}", formDto.getCode());

            return formDto;
        }

        formDto.setCode(taskFormDto.getKey());
        formDto.setRedirect(contentFormDto.isRedirect());
        formDto.setUrl(contentFormDto.getUrl());
        formDto.setContent(contentFormDto.getContent());

        return formDto;
    }

    public FormDTO findStartEventForm(FirstTaskForm firstTaskForm) {
        ProcessDefinition processDefinition = processEngine
                .getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionId(firstTaskForm.getProcessDefinitionId())
                .singleResult();
        List<BpmConfForm> bpmConfForms = bpmConfFormManager
                .find("from BpmConfForm where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        firstTaskForm.getProcessDefinitionId(),
                        firstTaskForm.getActivityId());
        FormDTO formDto = new FormDTO();
        formDto.setProcessDefinitionId(firstTaskForm.getProcessDefinitionId());
        formDto.setActivityId(firstTaskForm.getActivityId());
        formDto.setCode(firstTaskForm.getFormKey());

        if (!bpmConfForms.isEmpty()) {
            BpmConfForm bpmConfForm = bpmConfForms.get(0);

            if (!Integer.valueOf(2).equals(bpmConfForm.getStatus())) {
                if (Integer.valueOf(1).equals(bpmConfForm.getType())) {
                    formDto.setRedirect(true);
                    formDto.setUrl(bpmConfForm.getValue());
                } else {
                    formDto.setCode(bpmConfForm.getValue());
                }
            }
        } else {
            logger.info("cannot find bpmConfForm : {}, {}",
                    firstTaskForm.getProcessDefinitionId(),
                    formDto.getActivityId());
        }

        FormDTO contentFormDto = formConnector.findForm(formDto.getCode(),
                processDefinition.getTenantId());

        if (contentFormDto == null) {
            logger.error("cannot find form : {}", formDto.getCode());

            return formDto;
        }

        formDto.setRedirect(contentFormDto.isRedirect());
        formDto.setUrl(contentFormDto.getUrl());
        formDto.setContent(contentFormDto.getContent());

        return formDto;
    }

    /**
     * 未结流程.
     */
    public Page findRunningProcessInstances(String userId, String tenantId,
            Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        // TODO: 改成通过runtime表搜索，提高效率
        long count = historyService.createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).startedBy(userId)
                .unfinished().count();
        HistoricProcessInstanceQuery query = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).startedBy(userId)
                .orderByProcessInstanceStartTime().desc().unfinished();

        if (page.getOrderBy() != null) {
            String orderBy = page.getOrderBy();

            if ("processInstanceStartTime".equals(orderBy)) {
                query.orderByProcessInstanceStartTime();
            }

            if (page.isAsc()) {
                query.asc();
            } else {
                query.desc();
            }
        }

        List<HistoricProcessInstance> historicProcessInstances = query
                .listPage((int) page.getStart(), page.getPageSize());

        page.setResult(historicProcessInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 已结流程.
     */
    public Page findCompletedProcessInstances(String userId, String tenantId,
            Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).startedBy(userId).finished()
                .count();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery().startedBy(userId)
                .processInstanceTenantId(tenantId).finished()
                .orderByProcessInstanceStartTime().desc()
                .listPage((int) page.getStart(), page.getPageSize());

        page.setResult(historicProcessInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 参与流程.
     */
    public Page findInvolvedProcessInstances(String userId, String tenantId,
            Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        // TODO: finished(), unfinished()
        long count = historyService.createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).involvedUser(userId).count();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).involvedUser(userId)
                .orderByProcessInstanceStartTime().desc()
                .listPage((int) page.getStart(), page.getPageSize());

        page.setResult(historicProcessInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 待办任务（个人任务）.
     */
    public Page findPersonalTasks(String userId, String tenantId, Page page) {
        TaskService taskService = processEngine.getTaskService();

        long count = taskService.createTaskQuery().taskTenantId(tenantId)
                .taskAssignee(userId).active().count();
        List<Task> tasks = taskService.createTaskQuery().taskTenantId(tenantId)
                .taskAssignee(userId).active()
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(tasks);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 代领任务（组任务）.
     */
    public Page findGroupTasks(String userId, String tenantId, Page page) {
        TaskService taskService = processEngine.getTaskService();

        long count = taskService.createTaskQuery().taskTenantId(tenantId)
                .taskCandidateUser(userId).active().count();
        List<Task> tasks = taskService.createTaskQuery().taskTenantId(tenantId)
                .taskCandidateUser(userId).active()
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(tasks);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 已办任务（历史任务）.
     */
    public Page findHistoryTasks(String userId, String tenantId, Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricTaskInstanceQuery()
                .taskTenantId(tenantId).taskAssignee(userId).finished().count();
        List<HistoricTaskInstance> historicTaskInstances = historyService
                .createHistoricTaskInstanceQuery().taskTenantId(tenantId)
                .taskAssignee(userId).finished()
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(historicTaskInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 代理中的任务（代理人还未完成该任务）.
     */
    public Page findDelegatedTasks(String userId, String tenantId, Page page) {
        TaskService taskService = processEngine.getTaskService();

        long count = taskService.createTaskQuery().taskTenantId(tenantId)
                .taskOwner(userId).taskDelegationState(DelegationState.PENDING)
                .count();
        List<Task> tasks = taskService.createTaskQuery().taskTenantId(tenantId)
                .taskOwner(userId).taskDelegationState(DelegationState.PENDING)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(tasks);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 同时返回已领取和未领取的任务.
     */
    public Page findCandidateOrAssignedTasks(String userId, String tenantId,
            Page page) {
        TaskService taskService = processEngine.getTaskService();

        long count = taskService.createTaskQuery().taskTenantId(tenantId)
                .taskCandidateOrAssigned(userId).count();
        List<Task> tasks = taskService.createTaskQuery().taskTenantId(tenantId)
                .taskCandidateOrAssigned(userId)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(tasks);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 流程定义.
     */
    public Page findProcessDefinitions(String tenantId, Page page) {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        long count = repositoryService.createProcessDefinitionQuery()
                .processDefinitionTenantId(tenantId).count();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionTenantId(tenantId)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(processDefinitions);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 流程实例.
     */
    public Page findProcessInstances(String tenantId, Page page) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        long count = runtimeService.createProcessInstanceQuery()
                .processInstanceTenantId(tenantId).count();
        List<ProcessInstance> processInstances = runtimeService
                .createProcessInstanceQuery().processInstanceTenantId(tenantId)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(processInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 任务.
     */
    public Page findTasks(String tenantId, Page page) {
        TaskService taskService = processEngine.getTaskService();
        long count = taskService.createTaskQuery().taskTenantId(tenantId)
                .count();
        List<Task> tasks = taskService.createTaskQuery().taskTenantId(tenantId)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(tasks);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 部署.
     */
    public Page findDeployments(String tenantId, Page page) {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        long count = repositoryService.createDeploymentQuery()
                .deploymentTenantId(tenantId).count();
        List<Deployment> deployments = repositoryService
                .createDeploymentQuery().deploymentTenantId(tenantId)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(deployments);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 历史流程实例.
     */
    public Page findHistoricProcessInstances(String tenantId, Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).count();
        List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(historicProcessInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 历史节点.
     */
    public Page findHistoricActivityInstances(String tenantId, Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricActivityInstanceQuery()
                .activityTenantId(tenantId).count();
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery()
                .activityTenantId(tenantId)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(historicActivityInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 历史任务.
     */
    public Page findHistoricTaskInstances(String tenantId, Page page) {
        HistoryService historyService = processEngine.getHistoryService();

        long count = historyService.createHistoricTaskInstanceQuery()
                .taskTenantId(tenantId).count();
        List<HistoricTaskInstance> historicTaskInstances = historyService
                .createHistoricTaskInstanceQuery().taskTenantId(tenantId)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(historicTaskInstances);
        page.setTotalCount(count);

        return page;
    }

    /**
     * 作业.
     */
    public Page findJobs(String tenantId, Page page) {
        ManagementService managementService = processEngine
                .getManagementService();

        long count = managementService.createJobQuery().jobTenantId(tenantId)
                .count();
        List<Job> jobs = managementService.createJobQuery()
                .jobTenantId(tenantId)
                .listPage((int) page.getStart(), page.getPageSize());
        page.setResult(jobs);

        page.setTotalCount(count);

        return page;
    }

    /**
     * 根据processInstanceId获取businessKey.
     */
    public String findBusinessKeyByProcessInstanceId(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = processEngine
                .getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .processInstanceTenantId("1").singleResult();

        if (historicProcessInstance == null) {
            logger.info("cannot find processInstance : {}", processInstanceId);
            throw new IllegalStateException("cannot find process instance : "
                    + processInstanceId);
        }

        return historicProcessInstance.getBusinessKey();
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmConfFormManager(BpmConfFormManager bpmConfFormManager) {
        this.bpmConfFormManager = bpmConfFormManager;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Resource
    public void setFormConnector(FormConnector formConnector) {
        this.formConnector = formConnector;
    }

    @Resource
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }
}
