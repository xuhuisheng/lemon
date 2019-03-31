package com.mossle.bpm.listener;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfListener;
import com.mossle.bpm.persistence.manager.BpmConfListenerManager;

import com.mossle.spi.process.InternalProcessConnector;

import org.activiti.engine.delegate.event.ActivitiActivityEvent;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.delegate.event.ActivitiCancelledEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionEventListener implements ActivitiEventListener {
    private static Logger logger = LoggerFactory
            .getLogger(FunctionEventListener.class);
    private InternalProcessConnector internalProcessConnector;

    @Override
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {
        case ACTIVITY_STARTED:
            this.onActivityStart((ActivitiActivityEvent) event);

            break;

        case ACTIVITY_COMPLETED:
            this.onActivityEnd((ActivitiActivityEvent) event);

            break;

        case TASK_COMPLETED:
            this.onTaskCompleted((ActivitiEntityEvent) event);

            break;

        case PROCESS_COMPLETED:
            this.onProcessCompleted((ActivitiEntityEvent) event);

            break;

        case PROCESS_CANCELLED:
            this.onProcessCancelled((ActivitiCancelledEvent) event);

            break;

        default:
            logger.debug("Event received: {}", event.getType());
        }
    }

    public void onActivityStart(ActivitiActivityEvent event) {
        logger.debug("activity start {}", event);

        String processInstanceId = event.getProcessInstanceId();
        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager()
                .findExecutionById(processInstanceId);
        String businessKey = executionEntity.getBusinessKey();
        String processDefinitionId = event.getProcessDefinitionId();
        String activityId = event.getActivityId();
        String activityName = this.findActivityName(activityId,
                processDefinitionId);
        int eventCode = 0;
        String eventName = "start";
        String userId = Authentication.getAuthenticatedUserId();
        this.invokeExpression(eventCode, eventName, businessKey, userId,
                activityId, activityName);
    }

    public void onActivityEnd(ActivitiActivityEvent event) {
        logger.debug("activity end {}", event);

        String processInstanceId = event.getProcessInstanceId();
        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager()
                .findExecutionById(processInstanceId);
        String businessKey = executionEntity.getBusinessKey();
        String processDefinitionId = event.getProcessDefinitionId();
        String activityId = event.getActivityId();
        String activityName = this.findActivityName(activityId,
                processDefinitionId);
        int eventCode = 1;
        String eventName = "end";
        String userId = Authentication.getAuthenticatedUserId();
        this.invokeExpression(eventCode, eventName, businessKey, userId,
                activityId, activityName);
    }

    public void onTaskCompleted(ActivitiEntityEvent event) {
        logger.debug("task completed {}", event);

        String processInstanceId = event.getProcessInstanceId();
        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager()
                .findExecutionById(processInstanceId);
        String businessKey = executionEntity.getBusinessKey();
        String processDefinitionId = event.getProcessDefinitionId();
        Task task = (Task) event.getEntity();
        String activityId = task.getTaskDefinitionKey();
        String activityName = this.findActivityName(activityId,
                processDefinitionId);
        int eventCode = 5;
        String eventName = "complete";
        String userId = Authentication.getAuthenticatedUserId();
        this.invokeExpression(eventCode, eventName, businessKey, userId,
                activityId, activityName);
    }

    public void onProcessCompleted(ActivitiEntityEvent event) {
        logger.debug("process completed {}", event);

        String processInstanceId = event.getProcessInstanceId();
        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager()
                .findExecutionById(processInstanceId);
        String businessKey = executionEntity.getBusinessKey();
        String processDefinitionId = event.getProcessDefinitionId();
        String activityId = "";
        String activityName = this.findActivityName(activityId,
                processDefinitionId);
        int eventCode = 24;
        String eventName = "process-end";
        String userId = Authentication.getAuthenticatedUserId();
        this.invokeExpression(eventCode, eventName, businessKey, userId,
                activityId, activityName);
    }

    public void onProcessCancelled(ActivitiCancelledEvent event) {
        logger.debug("process cancelled {}", event);

        String processInstanceId = event.getProcessInstanceId();
        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager()
                .findExecutionById(processInstanceId);
        String businessKey = executionEntity.getBusinessKey();
        String processDefinitionId = event.getProcessDefinitionId();
        String activityId = "";
        String activityName = this.findActivityName(activityId,
                processDefinitionId);
        int eventCode = 23;
        String eventName = "process-close";
        String userId = Authentication.getAuthenticatedUserId();
        this.invokeExpression(eventCode, eventName, businessKey, userId,
                activityId, activityName);
    }

    public void invokeExpression(int eventCode, String eventName,
            String businessKey, String userId, String activityId,
            String activityName) {
        String processDefinitionId = "";
        logger.info("{} {} {}", processDefinitionId, activityId, eventCode);
        internalProcessConnector.fireEvent(eventName, businessKey, userId,
                activityId, activityName);
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    public String findActivityName(String activityId, String processDefinitionId) {
        if ("".equals(activityId)) {
            return "";
        }

        ProcessDefinitionEntity processDefinition = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId).execute(Context.getCommandContext());

        return (String) processDefinition.findActivity(activityId).getProperty(
                "name");
    }

    @Resource
    public void setInternalProcessConnector(
            InternalProcessConnector internalProcessConnector) {
        this.internalProcessConnector = internalProcessConnector;
    }
}
