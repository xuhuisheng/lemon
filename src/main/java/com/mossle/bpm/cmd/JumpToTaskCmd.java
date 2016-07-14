package com.mossle.bpm.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

public class JumpToTaskCmd implements Command<Object> {
    private String historyTaskId;
    private String executionId;
    private String jumpOrigin;

    public JumpToTaskCmd(String executionId, String historyTaskId) {
        this(executionId, historyTaskId, "jumpToTask");
    }

    public JumpToTaskCmd(String executionId, String historyTaskId,
            String jumpOrigin) {
        this.historyTaskId = historyTaskId;
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
        HistoricTaskInstanceEntity historicTaskInstance = commandContext
                .getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(historyTaskId);
        ActivityImpl activity = processDefinition
                .findActivity(historicTaskInstance.getTaskDefinitionKey());

        executionEntity.executeActivity(activity);

        for (TaskEntity taskEntity : commandContext.getTaskEntityManager()
                .findTasksByExecutionId(executionId)) {
            taskEntity.setAssignee(historicTaskInstance.getAssignee());
        }

        return null;
    }
}
