package com.mossle.bridge.process;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.form.FormDTO;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;

import com.mossle.bpm.cmd.FindStartFormCmd;
import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.page.Page;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.manager.FormTemplateManager;

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
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.MultiValueMap;

public class ProcessConnectorImpl implements ProcessConnector {
    private Logger logger = LoggerFactory.getLogger(ProcessConnectorImpl.class);
    private ProcessEngine processEngine;
    private BpmConfFormManager bpmConfFormManager;
    private FormTemplateManager formTemplateManager;
    private BpmProcessManager bpmProcessManager;

    public String startProcess(String userId, String businessKey,
            String processDefinitionId, Map<String, Object> processParameters) {
        // 先设置登录用户
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(userId);

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceById(processDefinitionId, businessKey,
                        processParameters);

        return processInstance.getId();
    }

    public ProcessDTO findProcess(String processId) {
        ProcessDTO processDto = new ProcessDTO();
        BpmProcess bpmProcess = bpmProcessManager
                .get(Long.parseLong(processId));
        String processDefinitionId = bpmProcess.getBpmConfBase()
                .getProcessDefinitionId();
        processDto.setProcessDefinitionId(processDefinitionId);
        processDto.setConfigTask(Integer.valueOf(1).equals(
                bpmProcess.getUseTaskConf()));

        return processDto;
    }

    /**
     * 获得启动表单.
     */
    public FormDTO findStartForm(String processDefinitionId) {
        FormDTO formDto = processEngine.getManagementService().executeCommand(
                new FindStartFormCmd(processDefinitionId));

        List<BpmConfForm> bpmConfForms = bpmConfFormManager
                .find("from BpmConfForm where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        formDto.getProcessDefinitionId(),
                        formDto.getActivityId());

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
        }

        FormTemplate formTemplate = formTemplateManager.findUniqueBy("code",
                formDto.getCode());

        if (formTemplate == null) {
            logger.error("cannot find form : {}", formDto.getCode());

            return formDto;
        }

        if (Integer.valueOf(1).equals(formTemplate.getType())) {
            formDto.setRedirect(true);
            formDto.setUrl(formTemplate.getContent());
        }

        return formDto;
    }

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

    @Resource
    public void setBpmConfFormManager(BpmConfFormManager bpmConfFormManager) {
        this.bpmConfFormManager = bpmConfFormManager;
    }

    @Resource
    public void setFormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }
}
