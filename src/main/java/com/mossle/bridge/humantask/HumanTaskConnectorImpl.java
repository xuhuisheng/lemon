package com.mossle.bridge.humantask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.humantask.HumanTaskDefinition;

import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;
import com.mossle.bpm.cmd.DeleteTaskWithCommentCmd;
import com.mossle.bpm.cmd.FindTaskDefinitionsCmd;
import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.domain.BpmTaskConf;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;
import com.mossle.bpm.persistence.manager.BpmTaskConfManager;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.manager.FormTemplateManager;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class HumanTaskConnectorImpl implements HumanTaskConnector {
    private Logger logger = LoggerFactory
            .getLogger(HumanTaskConnectorImpl.class);
    private ProcessEngine processEngine;
    private BpmConfOperationManager bpmConfOperationManager;
    private BpmConfFormManager bpmConfFormManager;
    private FormTemplateManager formTemplateManager;
    private BpmTaskConfManager bpmTaskConfManager;
    private JdbcTemplate jdbcTemplate;

    public HumanTaskDTO findHumanTask(String taskId) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        HumanTaskDTO humanTaskDto = new HumanTaskDTO();
        humanTaskDto.setId(task.getId());
        humanTaskDto.setProcessInstanceId(task.getProcessInstanceId());
        humanTaskDto.setProcessDefinitionId(task.getProcessDefinitionId());
        humanTaskDto.setTaskDefinitionKey(task.getTaskDefinitionKey());

        return humanTaskDto;
    }

    public FormDTO findTaskForm(String taskId) {
        HumanTaskDTO humanTaskDto = this.findHumanTask(taskId);

        FormDTO formDto = new FormDTO();
        formDto.setTaskId(taskId);

        List<BpmConfOperation> bpmConfOperations = bpmConfOperationManager
                .find("from BpmConfOperation where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        humanTaskDto.getProcessDefinitionId(),
                        humanTaskDto.getTaskDefinitionKey());

        for (BpmConfOperation bpmConfOperation : bpmConfOperations) {
            formDto.getButtons().add(bpmConfOperation.getValue());
        }

        String processDefinitionId = humanTaskDto.getProcessDefinitionId();
        String activityId = humanTaskDto.getTaskDefinitionKey();
        formDto.setProcessDefinitionId(processDefinitionId);
        formDto.setActivityId(activityId);

        List<BpmConfForm> bpmConfForms = bpmConfFormManager
                .find("from BpmConfForm where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, activityId);

        if (!bpmConfForms.isEmpty()) {
            BpmConfForm bpmConfForm = bpmConfForms.get(0);

            if (!Integer.valueOf(2).equals(bpmConfForm.getStatus())) {
                // 外部表单
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

    public List<HumanTaskDefinition> findHumanTaskDefinitions(
            String processDefinitionId) {
        FindTaskDefinitionsCmd cmd = new FindTaskDefinitionsCmd(
                processDefinitionId);
        List<TaskDefinition> taskDefinitions = processEngine
                .getManagementService().executeCommand(cmd);

        List<HumanTaskDefinition> humanTaskDefinitions = new ArrayList<HumanTaskDefinition>();

        for (TaskDefinition taskDefinition : taskDefinitions) {
            HumanTaskDefinition humanTaskDefinition = new HumanTaskDefinition();
            humanTaskDefinition.setKey(taskDefinition.getKey());
            humanTaskDefinition.setName(taskDefinition.getNameExpression()
                    .getExpressionText());
            humanTaskDefinition.setAssignee(taskDefinition
                    .getAssigneeExpression().getExpressionText());
            humanTaskDefinitions.add(humanTaskDefinition);
        }

        return humanTaskDefinitions;
    }

    public void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssignees) {
        if (taskDefinitionKeys == null) {
            return;
        }

        // 如果是从配置任务负责人的页面过来，就保存TaskConf，再从草稿中得到数据启动流程
        int index = 0;

        for (String taskDefinitionKey : taskDefinitionKeys) {
            String taskAssignee = taskAssignees.get(index++);
            BpmTaskConf bpmTaskConf = new BpmTaskConf();
            bpmTaskConf.setBusinessKey(businessKey);
            bpmTaskConf.setTaskDefinitionKey(taskDefinitionKey);
            bpmTaskConf.setAssignee(taskAssignee);
            bpmTaskConfManager.save(bpmTaskConf);
        }
    }

    public void completeTask(String taskId, String userId,
            Map<String, Object> taskParameters) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        // 处理抄送任务
        if ("copy".equals(task.getCategory())) {
            processEngine.getManagementService().executeCommand(
                    new DeleteTaskWithCommentCmd(taskId, "已阅"));

            return;
        }

        // 先设置登录用户
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(userId);

        if (task == null) {
            throw new IllegalStateException("任务不存在");
        }

        // logger.info("{}", task.getDelegationState());

        // 处理委办任务
        if (DelegationState.PENDING == task.getDelegationState()) {
            taskService.resolveTask(taskId);

            return;
        }

        // 处理子任务
        if ("subtask".equals(task.getCategory())) {
            processEngine.getManagementService().executeCommand(
                    new DeleteTaskWithCommentCmd(taskId, "完成"));

            int count = jdbcTemplate.queryForObject(
                    "select count(*) from ACT_RU_TASK where PARENT_TASK_ID_=?",
                    Integer.class, task.getParentTaskId());

            if (count > 1) {
                return;
            }

            taskId = task.getParentTaskId();
        }

        processEngine.getManagementService().executeCommand(
                new CompleteTaskWithCommentCmd(taskId, taskParameters, "完成"));
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmConfOperationManager(
            BpmConfOperationManager bpmConfOperationManager) {
        this.bpmConfOperationManager = bpmConfOperationManager;
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
    public void setBpmTaskConfManager(BpmTaskConfManager bpmTaskConfManager) {
        this.bpmTaskConfManager = bpmTaskConfManager;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
