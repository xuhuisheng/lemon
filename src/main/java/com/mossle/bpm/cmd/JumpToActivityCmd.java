package com.mossle.bpm.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

public class JumpToActivityCmd implements Command<Object> {
    private String activityId;
    private String executionId;
    private String jumpOrigin;

    public JumpToActivityCmd(String executionId, String activityId) {
        this(executionId, activityId, "jumpToActivity");
    }

    public JumpToActivityCmd(String executionId, String activityId,
            String jumpOrigin) {
        this.activityId = activityId;
        this.executionId = executionId;
        this.jumpOrigin = jumpOrigin;
    }

    public Object execute(CommandContext commandContext) {
        for (TaskEntity taskEntity : commandContext.getTaskEntityManager()
                .findTasksByExecutionId(executionId)) {
            taskEntity.setVariableLocal("跳转原因", jumpOrigin);
            commandContext.getTaskEntityManager().deleteTask(taskEntity,
                    jumpOrigin, false);
        }

        ExecutionEntity executionEntity = commandContext
                .getExecutionEntityManager().findExecutionById(executionId);
        ProcessDefinitionImpl processDefinition = executionEntity
                .getProcessDefinition();
        ActivityImpl activity = processDefinition.findActivity(activityId);

        executionEntity.executeActivity(activity);

        return null;
    }
}
