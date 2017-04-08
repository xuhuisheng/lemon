package com.mossle.bpm.listener;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskConstants;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.humantask.ParticipantDTO;
import com.mossle.api.user.UserConnector;

import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;
import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.support.DefaultTaskListener;
import com.mossle.bpm.support.DelegateTaskHolder;
import com.mossle.bpm.support.HumanTaskBuilder;

import com.mossle.core.mapper.BeanMapper;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.BaseEntityEventListener;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.IdentityLink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoCompleteFirstTaskEventListener implements
        ActivitiEventListener {
    public static final int TYPE_COPY = 3;
    private static Logger logger = LoggerFactory
            .getLogger(AutoCompleteFirstTaskEventListener.class);
    private HumanTaskConnector humanTaskConnector;

    public void onEvent(ActivitiEvent event) {
        if (!(event instanceof ActivitiEntityEventImpl)) {
            return;
        }

        ActivitiEntityEventImpl activitiEntityEventImpl = (ActivitiEntityEventImpl) event;
        Object entity = activitiEntityEventImpl.getEntity();

        if (!(entity instanceof TaskEntity)) {
            return;
        }

        TaskEntity taskEntity = (TaskEntity) entity;

        try {
            switch (event.getType()) {
            case TASK_CREATED:
                logger.debug("create : {}", taskEntity.getId());
                this.onCreate(taskEntity);

                break;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void onCreate(DelegateTask delegateTask) throws Exception {
        String initiatorId = Authentication.getAuthenticatedUserId();

        if (initiatorId == null) {
            return;
        }

        String assignee = delegateTask.getAssignee();

        if (assignee == null) {
            return;
        }

        if (!initiatorId.equals(assignee)) {
            return;
        }

        PvmActivity targetActivity = this.findFirstActivity(delegateTask
                .getProcessDefinitionId());
        logger.debug("targetActivity : {}", targetActivity);

        if (!targetActivity.getId().equals(
                delegateTask.getExecution().getCurrentActivityId())) {
            return;
        }

        logger.debug("auto complete first task : {}", delegateTask);

        for (IdentityLink identityLink : delegateTask.getCandidates()) {
            String userId = identityLink.getUserId();
            String groupId = identityLink.getGroupId();

            if (userId != null) {
                delegateTask.deleteCandidateUser(userId);
            }

            if (groupId != null) {
                delegateTask.deleteCandidateGroup(groupId);
            }
        }

        // 对提交流程的任务进行特殊处理
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTaskByTaskId(delegateTask.getId());
        humanTaskDto.setCatalog(HumanTaskConstants.CATALOG_START);
        humanTaskConnector.saveHumanTask(humanTaskDto);

        // ((TaskEntity) delegateTask).complete();
        // Context.getCommandContext().getHistoryManager().recordTaskId((TaskEntity) delegateTask);
        // Context.getCommandContext().getHistoryManager().recordTaskId((TaskEntity) delegateTask);
        // new CompleteTaskWithCommentCmd(delegateTask.getId(), null, "发起流程")
        // .execute(Context.getCommandContext());

        // 因为recordTaskId()会判断endTime，而complete以后会导致endTime!=null，
        // 所以才会出现record()放在complete后面导致taskId没记录到historyActivity里的情况
        delegateTask.getExecution().setVariableLocal(
                "_ACTIVITI_SKIP_EXPRESSION_ENABLED", true);

        TaskDefinition taskDefinition = ((TaskEntity) delegateTask)
                .getTaskDefinition();
        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();
        Expression expression = expressionManager
                .createExpression("${_ACTIVITI_SKIP_EXPRESSION_ENABLED}");
        taskDefinition.setSkipExpression(expression);
    }

    /**
     * 获得第一个节点.
     */
    public PvmActivity findFirstActivity(String processDefinitionId) {
        ProcessDefinitionEntity processDefinitionEntity = Context
                .getProcessEngineConfiguration().getProcessDefinitionCache()
                .get(processDefinitionId);

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
            logger.debug("first activity is not userTask, just skip");

            return null;
        }

        return targetActivity;
    }

    public boolean isFailOnException() {
        return false;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }
}
