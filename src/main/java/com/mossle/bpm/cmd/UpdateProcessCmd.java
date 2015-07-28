package com.mossle.bpm.cmd;

import java.io.InputStream;

import org.activiti.bpmn.model.BpmnModel;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class UpdateProcessCmd implements Command<Void> {
    private static Logger logger = LoggerFactory
            .getLogger(UpdateProcessCmd.class);
    private String processDefinitionId;
    private byte[] bytes;

    public UpdateProcessCmd(String processDefinitionId, byte[] bytes) {
        this.processDefinitionId = processDefinitionId;
        this.bytes = bytes;
    }

    public Void execute(CommandContext commandContext) {
        ProcessDefinitionEntity processDefinitionEntity = commandContext
                .getProcessDefinitionEntityManager().findProcessDefinitionById(
                        processDefinitionId);
        String resourceName = processDefinitionEntity.getResourceName();
        String deploymentId = processDefinitionEntity.getDeploymentId();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(Context
                .getProcessEngineConfiguration().getDataSource());
        jdbcTemplate
                .update("update ACT_GE_BYTEARRAY set BYTES_=? where NAME_=? and DEPLOYMENT_ID_=?",
                        bytes, resourceName, deploymentId);

        Context.getProcessEngineConfiguration().getProcessDefinitionCache()
                .remove(processDefinitionId);

        try {
            // update png
            GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(
                    processDefinitionId);
            BpmnModel bpmnModel = getBpmnModelCmd.execute(commandContext);
            ProcessEngineConfiguration processEngineConfiguration = Context
                    .getProcessEngineConfiguration();
            ProcessDefinitionDiagramCmd processDefinitionDiagramCmd = new ProcessDefinitionDiagramCmd(
                    processDefinitionEntity.getId());
            InputStream is = processDefinitionDiagramCmd
                    .execute(commandContext);
            byte[] pngBytes = IOUtils.toByteArray(is);
            String diagramResourceName = processDefinitionEntity
                    .getDiagramResourceName();
            jdbcTemplate
                    .update("update ACT_GE_BYTEARRAY set BYTES_=? where NAME_=? and DEPLOYMENT_ID_=?",
                            pngBytes, diagramResourceName, deploymentId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }
}
