package com.mossle.bpm.cmd;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.InterpretableExecution;

public class ReOpenProcessCmd implements Command<Void> {
    private String historicProcessInstanceId;

    public ReOpenProcessCmd(String historicProcessInstanceId) {
        this.historicProcessInstanceId = historicProcessInstanceId;
    }

    public Void execute(CommandContext commandContext) {
        HistoricProcessInstanceEntity historicProcessInstanceEntity = commandContext
                .getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(historicProcessInstanceId);
        historicProcessInstanceEntity.setEndActivityId(null);
        historicProcessInstanceEntity.setEndTime(null);

        String processDefinitionId = historicProcessInstanceEntity
                .getProcessDefinitionId();
        String initiator = historicProcessInstanceEntity.getStartUserId();
        String businessKey = historicProcessInstanceEntity.getBusinessKey();

        ProcessDefinitionEntity processDefinition = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId).execute(commandContext);

        // ExecutionEntity processInstance = processDefinition
        // .createProcessInstance(businessKey);
        ExecutionEntity processInstance = this.createProcessInstance(
                historicProcessInstanceEntity.getId(), businessKey, initiator,
                processDefinition);

        try {
            Authentication.setAuthenticatedUserId(initiator);
            // start
            processInstance.start();
        } finally {
            Authentication.setAuthenticatedUserId(null);
        }

        return null;
    }

    public ExecutionEntity createProcessInstance(String processInstanceId,
            String businessKey, String authenticatedUserId,
            ProcessDefinitionEntity processDefinition) {
        // ExecutionEntity processInstance = (ExecutionEntity) this
        // .createProcessInstance(processDefinition);
        // ExecutionEntity processInstance = (ExecutionEntity) processDefinition
        // .createProcessInstanceForInitial(processDefinition.getInitial());
        // processInstance.setId(processInstanceId);
        ExecutionEntity processInstance = (ExecutionEntity) this
                .createProcessInstance(processDefinition, processInstanceId);
        processInstance.setExecutions(new ArrayList<ExecutionEntity>());
        processInstance.setProcessDefinition(processDefinition);

        // Do not initialize variable map (let it happen lazily)
        if (businessKey != null) {
            processInstance.setBusinessKey(businessKey);
        }

        // Reset the process instance in order to have the db-generated process instance id available
        processInstance.setProcessInstance(processInstance);

        String initiatorVariableName = (String) processDefinition
                .getProperty(BpmnParse.PROPERTYNAME_INITIATOR_VARIABLE_NAME);

        if (initiatorVariableName != null) {
            processInstance.setVariable(initiatorVariableName,
                    authenticatedUserId);
        }

        // if (authenticatedUserId != null) {
        // processInstance.addIdentityLink(authenticatedUserId,
        // IdentityLinkType.STARTER);
        // }

        // Context.getCommandContext().getHistoryManager()
        // .recordProcessInstanceStart(processInstance);
        return processInstance;
    }

    public ExecutionEntity createProcessInstance(
            ProcessDefinitionEntity processDefinition, String id) {
        ActivityImpl initial = processDefinition.getInitial();

        if (initial == null) {
            throw new ActivitiException(
                    "Cannot start process instance, initial activity where the process instance should start is null.");
        }

        ExecutionEntity processInstance = new ExecutionEntity(initial);
        processInstance.setId(id);
        processInstance.insert();
        processInstance.setProcessDefinition(processDefinition);
        processInstance.setTenantId(processDefinition.getTenantId());
        processInstance.setProcessInstance(processInstance);
        processInstance.initialize();

        InterpretableExecution scopeInstance = processInstance;

        List<ActivityImpl> initialActivityStack = processDefinition
                .getInitialActivityStack(initial);

        for (ActivityImpl initialActivity : initialActivityStack) {
            if (initialActivity.isScope()) {
                scopeInstance = (InterpretableExecution) scopeInstance
                        .createExecution();
                scopeInstance.setActivity(initialActivity);

                if (initialActivity.isScope()) {
                    scopeInstance.initialize();
                }
            }
        }

        scopeInstance.setActivity(initial);

        return processInstance;
    }
}
