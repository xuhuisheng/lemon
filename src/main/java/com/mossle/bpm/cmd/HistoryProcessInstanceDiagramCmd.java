package com.mossle.bpm.cmd;

import java.io.*;

import java.util.*;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;

import org.activiti.engine.impl.bpmn.diagram.*;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class HistoryProcessInstanceDiagramCmd implements Command<InputStream> {
    protected String historyProcessInstanceId;

    public HistoryProcessInstanceDiagramCmd(String historyProcessInstanceId) {
        this.historyProcessInstanceId = historyProcessInstanceId;
    }

    public InputStream execute(CommandContext commandContext) {
        HistoricProcessInstanceEntityManager historicProcessInstanceEntityManager = commandContext
                .getHistoricProcessInstanceEntityManager();
        HistoricProcessInstanceEntity historicProcessInstanceEntity = historicProcessInstanceEntityManager
                .findHistoricProcessInstance(historyProcessInstanceId);

        List<String> activityIds = this
                .getActivityIdsFromHistoricProcessInstanceEntity(
                        historicProcessInstanceEntity, commandContext);

        String processDefinitionId = historicProcessInstanceEntity
                .getProcessDefinitionId();

        GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(
                processDefinitionId);
        BpmnModel bpmnModel = getBpmnModelCmd.execute(commandContext);

        InputStream is = ProcessDiagramGenerator.generateDiagram(bpmnModel,
                "png", activityIds);

        return is;
    }

    protected List<String> getActivityIdsFromHistoricProcessInstanceEntity(
            HistoricProcessInstanceEntity historicProcessInstanceEntity,
            CommandContext commandContext) {
        if (historicProcessInstanceEntity.getEndActivityId() == null) {
            String processInstanceId = historicProcessInstanceEntity.getId();
            ExecutionEntityManager executionEntityManager = commandContext
                    .getExecutionEntityManager();
            ExecutionEntity executionEntity = executionEntityManager
                    .findExecutionById(processInstanceId);
            List<String> activityIds;

            if (executionEntity != null) {
                activityIds = executionEntity.findActiveActivityIds();
            } else {
                activityIds = Collections.EMPTY_LIST;
            }

            return activityIds;
        } else {
            return Collections.singletonList(historicProcessInstanceEntity
                    .getEndActivityId());
        }
    }
}
