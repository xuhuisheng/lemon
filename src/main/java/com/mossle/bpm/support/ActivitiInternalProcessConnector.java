package com.mossle.bpm.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.form.FormDTO;

import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;
import com.mossle.bpm.cmd.DeleteTaskWithCommentCmd;
import com.mossle.bpm.cmd.FindTaskDefinitionsCmd;
import com.mossle.bpm.cmd.RollbackTaskCmd;
import com.mossle.bpm.cmd.WithdrawTaskCmd;
import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.domain.BpmTaskConf;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;
import com.mossle.bpm.persistence.manager.BpmTaskConfManager;

import com.mossle.spi.process.InternalProcessConnector;
import com.mossle.spi.process.ProcessTaskDefinition;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class ActivitiInternalProcessConnector implements
        InternalProcessConnector {
    private static Logger logger = LoggerFactory
            .getLogger(ActivitiInternalProcessConnector.class);
    private ProcessEngine processEngine;
    private BpmConfOperationManager bpmConfOperationManager;
    private BpmConfFormManager bpmConfFormManager;
    private BpmTaskConfManager bpmTaskConfManager;
    private JdbcTemplate jdbcTemplate;

    /**
     * 获得任务表单，不包含表单内容.
     */
    public FormDTO findTaskForm(String taskId) {
        Task task = processEngine.getTaskService().createTaskQuery()
                .taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        String activityId = task.getTaskDefinitionKey();
        FormDTO formDto = new FormDTO();
        formDto.setTaskId(taskId);

        List<BpmConfOperation> bpmConfOperations = bpmConfOperationManager
                .find("from BpmConfOperation where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, activityId);

        for (BpmConfOperation bpmConfOperation : bpmConfOperations) {
            formDto.getButtons().add(bpmConfOperation.getValue());
        }

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

        return formDto;
    }

    /**
     * 获得任务定义.
     */
    public List<ProcessTaskDefinition> findTaskDefinitions(
            String processDefinitionId) {
        List<ProcessTaskDefinition> processTaskDefinitions = new ArrayList<ProcessTaskDefinition>();
        FindTaskDefinitionsCmd cmd = new FindTaskDefinitionsCmd(
                processDefinitionId);
        List<TaskDefinition> taskDefinitions = processEngine
                .getManagementService().executeCommand(cmd);

        for (TaskDefinition taskDefinition : taskDefinitions) {
            ProcessTaskDefinition processTaskDefinition = new ProcessTaskDefinition();
            processTaskDefinition.setKey(taskDefinition.getKey());

            if (taskDefinition.getNameExpression() != null) {
                processTaskDefinition.setName(taskDefinition
                        .getNameExpression().getExpressionText());
            }

            if (taskDefinition.getAssigneeExpression() != null) {
                processTaskDefinition.setAssignee(taskDefinition
                        .getAssigneeExpression().getExpressionText());
            }

            processTaskDefinitions.add(processTaskDefinition);
        }

        return processTaskDefinitions;
    }

    /**
     * 配置任务定义.
     */
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

    /**
     * 完成任务.
     */
    public void completeTask(String taskId, String userId,
            Map<String, Object> variables) {
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new IllegalStateException("任务不存在");
        }

        // 先设置登录用户
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(userId);

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
                new CompleteTaskWithCommentCmd(taskId, variables, "完成"));
    }

    /**
     * 转发任务.
     */
    public void transfer(String taskId, String assignee, String owner) {
        processEngine.getTaskService().setAssignee(taskId, assignee);
        processEngine.getTaskService().setOwner(taskId, owner);
    }

    /**
     * 撤销任务.
     */
    public void withdrawTask(String taskId) {
        Command<Integer> cmd = new WithdrawTaskCmd(taskId);

        processEngine.getManagementService().executeCommand(cmd);
    }

    /**
     * 回退指定节点，指定负责人. 如果activityId为null，就自动查询上一个节点.
     */
    public void rollback(String taskId, String activityId, String userId) {
        Command<Object> cmd = new RollbackTaskCmd(taskId, activityId, userId);

        processEngine.getManagementService().executeCommand(cmd);
    }

    /**
     * 回退自定节点，使用最后的负责人. 如果activityId为null，就自动查询上一个节点.
     */
    public void rollbackAuto(String taskId, String activityId) {
        Command<Object> cmd = new RollbackTaskCmd(taskId, activityId);
        processEngine.getManagementService().executeCommand(cmd);
    }

    public void delegateTask(String taskId, String userId) {
        processEngine.getTaskService().delegateTask(taskId, userId);
    }

    public void resolveTask(String taskId) {
        processEngine.getTaskService().resolveTask(taskId);
    }

    // ~ ==================================================
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
    public void setBpmTaskConfManager(BpmTaskConfManager bpmTaskConfManager) {
        this.bpmTaskConfManager = bpmTaskConfManager;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
