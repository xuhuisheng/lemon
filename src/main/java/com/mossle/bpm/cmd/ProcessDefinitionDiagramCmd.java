package com.mossle.bpm.cmd;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.activiti.bpmn.model.BpmnModel;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

import org.activiti.image.impl.DefaultProcessDiagramGenerator;

public class ProcessDefinitionDiagramCmd implements Command<InputStream> {
    protected String processDefinitionId;

    public ProcessDefinitionDiagramCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public InputStream execute(CommandContext commandContext) {
        ProcessDefinitionEntity processDefinition = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId).execute(commandContext);
        String diagramResourceName = processDefinition.getDiagramResourceName();
        String deploymentId = processDefinition.getDeploymentId();

        if (deploymentId != null) {
            byte[] bytes = commandContext
                    .getResourceEntityManager()
                    .findResourceByDeploymentIdAndResourceName(deploymentId,
                            diagramResourceName).getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);

            return inputStream;
        }

        GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(
                processDefinitionId);
        BpmnModel bpmnModel = getBpmnModelCmd.execute(commandContext);
        ProcessEngineConfiguration processEngineConfiguration = Context
                .getProcessEngineConfiguration();
        InputStream is = new DefaultProcessDiagramGenerator().generateDiagram(
                bpmnModel, "png",
                processEngineConfiguration.getActivityFontName(),
                processEngineConfiguration.getLabelFontName(), null);

        return is;
    }
}
