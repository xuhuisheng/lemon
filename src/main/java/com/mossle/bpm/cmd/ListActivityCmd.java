package com.mossle.bpm.cmd;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListActivityCmd implements Command<Map<String, String>> {
    private static Logger logger = LoggerFactory
            .getLogger(ListActivityCmd.class);
    private String executionId;

    public ListActivityCmd(String executionId) {
        this.executionId = executionId;
    }

    public Map<String, String> execute(CommandContext commandContext) {
        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager().findExecutionById(executionId);
        ProcessDefinitionImpl processDefinition = executionEntity
                .getProcessDefinition();
        Map<String, String> map = new HashMap<String, String>();

        for (ActivityImpl activity : processDefinition.getActivities()) {
            logger.info("{}", activity.getProperties());

            if ("userTask".equals(activity.getProperty("type"))) {
                map.put(activity.getId(), (String) activity.getProperty("name"));
            }
        }

        return map;
    }
}
