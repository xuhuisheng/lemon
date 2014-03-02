package com.mossle.bpm.cmd;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindProcessDefinitionEntityCmd implements
        Command<ProcessDefinitionEntity> {
    private static Logger logger = LoggerFactory
            .getLogger(FindProcessDefinitionEntityCmd.class);
    private String processDefinitionId;

    public FindProcessDefinitionEntityCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public ProcessDefinitionEntity execute(CommandContext commandContext) {
        ProcessDefinitionEntity processDefinitionEntity = Context
                .getProcessEngineConfiguration().getDeploymentManager()
                .findDeployedProcessDefinitionById(processDefinitionId);

        if (processDefinitionEntity == null) {
            throw new IllegalArgumentException(
                    "cannot find processDefinition : " + processDefinitionId);
        }

        return processDefinitionEntity;
    }
}
