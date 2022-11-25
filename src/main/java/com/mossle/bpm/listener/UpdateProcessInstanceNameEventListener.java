package com.mossle.bpm.listener;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.client.user.UserClient;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.BaseEntityEventListener;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

public class UpdateProcessInstanceNameEventListener extends
        BaseEntityEventListener {
    private UserClient userClient;

    protected void onInitialized(ActivitiEvent event) {
        if (!(event instanceof ActivitiEntityEventImpl)) {
            return;
        }

        ActivitiEntityEventImpl activitiEntityEventImpl = (ActivitiEntityEventImpl) event;
        Object entity = activitiEntityEventImpl.getEntity();

        if (!(entity instanceof ExecutionEntity)) {
            return;
        }

        ActivitiEventType activitiEventType = activitiEntityEventImpl.getType();

        if (activitiEventType != ActivitiEventType.ENTITY_INITIALIZED) {
            return;
        }

        ExecutionEntity executionEntity = (ExecutionEntity) entity;

        if (!executionEntity.isProcessInstanceType()) {
            return;
        }

        String processInstanceId = executionEntity.getId();
        String processDefinitionId = executionEntity.getProcessDefinitionId();
        CommandContext commandContext = Context.getCommandContext();
        ProcessDefinitionEntity processDefinition = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId).execute(commandContext);

        // {流程标题:title}-{发起人:startUser}-{发起时间:startTime}
        String processDefinitionName = processDefinition.getName();

        if (processDefinitionName == null) {
            processDefinitionName = processDefinition.getKey();
        }

        String userId = Authentication.getAuthenticatedUserId();
        String displayName = userClient.findById(userId, "1").getDisplayName();
        String processInstanceName = processDefinitionName + "-" + displayName
                + "-"
                + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        // runtime
        executionEntity.setName(processInstanceName);

        // history
        HistoricProcessInstanceEntity historicProcessInstanceEntity = commandContext
                .getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(processInstanceId);
        historicProcessInstanceEntity.setName(processInstanceName);
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
