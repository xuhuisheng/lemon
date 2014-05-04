package com.mossle.bpm.cmd;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

import org.springframework.jdbc.core.JdbcTemplate;

public class UpdateProcessCmd implements Command<Void> {
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

        return null;
    }
}
