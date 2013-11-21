package com.mossle.bpm.cmd;

import java.io.InputStream;

import java.util.Collections;

import org.activiti.bpmn.model.BpmnModel;

import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;

public class ProcessDefinitionDiagramCmd implements Command<InputStream> {
    protected String processDefinitionId;

    public ProcessDefinitionDiagramCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public InputStream execute(CommandContext commandContext) {
        GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(
                processDefinitionId);
        BpmnModel bpmnModel = getBpmnModelCmd.execute(commandContext);

        InputStream is = ProcessDiagramGenerator.generateDiagram(bpmnModel,
                "png", Collections.EMPTY_LIST);

        return is;
    }
}
