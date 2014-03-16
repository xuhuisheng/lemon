package com.mossle.bpm.cmd;

import java.io.*;

import java.util.*;
import java.util.List;

import com.mossle.bpm.util.CustomProcessDiagramGenerator;

import org.activiti.bpmn.model.BpmnModel;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntityManager;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class HistoryProcessInstanceDiagramCmd implements Command<InputStream> {
    protected String historyProcessInstanceId;

    public HistoryProcessInstanceDiagramCmd(String historyProcessInstanceId) {
        this.historyProcessInstanceId = historyProcessInstanceId;
    }

    public InputStream execute(CommandContext commandContext) {
        try {
            CustomProcessDiagramGenerator customProcessDiagramGenerator = new CustomProcessDiagramGenerator();

            return customProcessDiagramGenerator
                    .generateDiagram(historyProcessInstanceId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
