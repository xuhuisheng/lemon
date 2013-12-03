package com.mossle.bpm.cmd;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.task.TaskDefinition;

public class FindTaskDefinitionsCmd implements Command<List<TaskDefinition>> {
    protected String processDefinitionId;

    public FindTaskDefinitionsCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public List<TaskDefinition> execute(CommandContext commandContext) {
        ProcessDefinitionEntity processDefinitionEntity = Context
                .getProcessEngineConfiguration().getProcessDefinitionCache()
                .get(processDefinitionId);
        List<TaskDefinition> taskDefinitions = new ArrayList<TaskDefinition>();
        taskDefinitions.addAll(processDefinitionEntity.getTaskDefinitions()
                .values());

        return taskDefinitions;
    }
}
