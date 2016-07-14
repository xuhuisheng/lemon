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
import com.mossle.bpm.cmd.SignalStartEventCmd;
import com.mossle.bpm.cmd.WithdrawTaskCmd;
import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.domain.BpmTaskConf;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.persistence.manager.BpmTaskConfManager;

import com.mossle.spi.process.InternalProcessConnector;
import com.mossle.spi.process.ProcessTaskDefinition;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 这是一个spi接口，因为HumanTask的配置还没有完全从bpm里抽象出去，所以还有不少配置需要从bpm里读取.
 */
public class ActivitiInternalProcessConnector implements
        InternalProcessConnector {
    /** logger. */
    private static Logger logger = LoggerFactory
            .getLogger(ActivitiInternalProcessConnector.class);
    private ProcessEngine processEngine;
    private BpmConfOperationManager bpmConfOperationManager;
    private BpmConfFormManager bpmConfFormManager;
    private BpmTaskConfManager bpmTaskConfManager;
    private BpmConfUserManager bpmConfUserManager;
    private JdbcTemplate jdbcTemplate;

    /**
     * 获得任务表单，不包含表单内容.
     */
    public FormDTO findTaskForm(String taskId) {
        if (taskId == null) {
            logger.error("taskId cannot be null");

            return null;
        }

        Task task = processEngine.getTaskService().createTaskQuery()
                .taskId(taskId).singleResult();

        if (task == null) {
            logger.error("cannot find task for {}", taskId);

            return null;
        }

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

    /**
     * 协办任务.
     */
    public void delegateTask(String taskId, String userId) {
        processEngine.getTaskService().delegateTask(taskId, userId);
    }

    /**
     * 完成协办.
     */
    public void resolveTask(String taskId) {
        processEngine.getTaskService().resolveTask(taskId);
    }

    /**
     * 根据activityId找到任务定义.
     * 
     * TODO: 支持ExpressionManager，支持Expr
     */
    public ProcessTaskDefinition findTaskDefinition(String processDefinitionId,
            String taskDefinitionKey, String businessKey) {
        // 先从流程定义里读取设计配置的负责人
        List<BpmConfUser> bpmConfUsers = bpmConfUserManager
                .find("from BpmConfUser where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, taskDefinitionKey);
        logger.debug("{}", bpmConfUsers);

        ProcessTaskDefinition processTaskDefinition = new ProcessTaskDefinition();

        try {
            for (BpmConfUser bpmConfUser : bpmConfUsers) {
                logger.debug("status : {}, type: {}", bpmConfUser.getStatus(),
                        bpmConfUser.getType());
                logger.debug("value : {}", bpmConfUser.getValue());

                String value = bpmConfUser.getValue();

                if (bpmConfUser.getStatus() == 1) {
                    if (bpmConfUser.getType() == 0) {
                        logger.debug("add assignee : {}", value);
                        processTaskDefinition.setAssignee(value);
                    } else if (bpmConfUser.getType() == 1) {
                        logger.debug("add candidate user : {}", value);
                        processTaskDefinition.addParticipantDefinition("user",
                                value, "add");
                    } else if (bpmConfUser.getType() == 2) {
                        logger.debug("add candidate group : {}", value);
                        processTaskDefinition.addParticipantDefinition("group",
                                value, "add");
                    }
                } else if (bpmConfUser.getStatus() == 2) {
                    if (bpmConfUser.getType() == 0) {
                        logger.debug("delete assignee : {}", value);
                        processTaskDefinition.setAssignee(null);
                    } else if (bpmConfUser.getType() == 1) {
                        logger.debug("delete candidate user : {}", value);
                        processTaskDefinition.addParticipantDefinition("user",
                                value, "delete");
                    } else if (bpmConfUser.getType() == 2) {
                        logger.debug("delete candidate group : {}", value);
                        processTaskDefinition.addParticipantDefinition("group",
                                value, "delete");
                    }
                }
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);
        }

        try {
            String hql = "from BpmTaskConf where businessKey=? and taskDefinitionKey=?";
            BpmTaskConf bpmTaskConf = bpmTaskConfManager.findUnique(hql,
                    businessKey, taskDefinitionKey);

            if (bpmTaskConf != null) {
                String assignee = bpmTaskConf.getAssignee();

                if ((assignee != null) && (!"".equals(assignee))) {
                    logger.debug("add assignee : {}", assignee);
                    processTaskDefinition.setAssignee(assignee);
                }
            } else {
                logger.info("cannot find BpmTaskConf {} {}", businessKey,
                        taskDefinitionKey);
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);
        }

        return processTaskDefinition;
    }

    /**
     * 获得流程发起人.
     */
    public String findInitiator(String processInstanceId) {
        String initiator = null;

        if (Context.getCommandContext() == null) {
            initiator = processEngine.getHistoryService()
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult()
                    .getStartUserId();
        } else {
            initiator = Context.getCommandContext()
                    .getHistoricProcessInstanceEntityManager()
                    .findHistoricProcessInstance(processInstanceId)
                    .getStartUserId();
        }

        return initiator;
    }

    /**
     * 获得某个节点的历史负责人.
     */
    public String findAssigneeByActivityId(String processInstanceId,
            String activityId) {
        return null;
    }

    /**
     * 解析表达式.
     */
    public Object executeExpression(String taskId, String expressionText) {
        TaskEntity taskEntity = Context.getCommandContext()
                .getTaskEntityManager().findTaskById(taskId);
        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();

        return expressionManager.createExpression(expressionText).getValue(
                taskEntity);
    }

    /**
     * 获得开始事件id.
     */
    public String findInitialActivityId(String processDefinitionId) {
        GetDeploymentProcessDefinitionCmd getDeploymentProcessDefinitionCmd = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId);
        ProcessDefinitionEntity processDefinition = processEngine
                .getManagementService().executeCommand(
                        getDeploymentProcessDefinitionCmd);

        return processDefinition.getInitial().getId();
    }

    /**
     * 获得提交节点
     */
    public String findFirstUserTaskActivityId(String processDefinitionId,
            String initiator) {
        GetDeploymentProcessDefinitionCmd getDeploymentProcessDefinitionCmd = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId);
        ProcessDefinitionEntity processDefinitionEntity = processEngine
                .getManagementService().executeCommand(
                        getDeploymentProcessDefinitionCmd);

        ActivityImpl startActivity = processDefinitionEntity.getInitial();

        if (startActivity.getOutgoingTransitions().size() != 1) {
            throw new IllegalStateException(
                    "start activity outgoing transitions cannot more than 1, now is : "
                            + startActivity.getOutgoingTransitions().size());
        }

        PvmTransition pvmTransition = startActivity.getOutgoingTransitions()
                .get(0);
        PvmActivity targetActivity = pvmTransition.getDestination();

        if (!"userTask".equals(targetActivity.getProperty("type"))) {
            logger.info("first activity is not userTask, just skip");

            return null;
        }

        return targetActivity.getId();
    }

    /**
     * 触发execution继续执行.
     */
    public void signalExecution(String executionId) {
        // processEngine.getRuntimeService().signal(executionId);
        processEngine.getManagementService().executeCommand(
                new SignalStartEventCmd(executionId));
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
    public void setBpmConfUserManager(BpmConfUserManager bpmConfUserManager) {
        this.bpmConfUserManager = bpmConfUserManager;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
