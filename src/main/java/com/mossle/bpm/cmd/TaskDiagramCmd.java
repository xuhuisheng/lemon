package com.mossle.bpm.cmd;

import java.io.*;

import java.util.*;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;

import org.activiti.engine.impl.bpmn.diagram.*;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

public class TaskDiagramCmd implements Command<InputStream> {
    protected String taskId;

    public TaskDiagramCmd(String taskId) {
        this.taskId = taskId;
    }

    public InputStream execute(CommandContext commandContext) {
        TaskEntityManager taskEntityManager = commandContext
                .getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findTaskById(taskId);

        ExecutionEntity executionEntity = taskEntity.getExecution();

        List<String> activityIds = executionEntity.findActiveActivityIds();
        String processDefinitionId = executionEntity.getProcessDefinitionId();

        GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(
                processDefinitionId);
        BpmnModel bpmnModel = getBpmnModelCmd.execute(commandContext);

        InputStream is = ProcessDiagramGenerator.generateDiagram(bpmnModel,
                "png", activityIds);

        return is;
    }
}
