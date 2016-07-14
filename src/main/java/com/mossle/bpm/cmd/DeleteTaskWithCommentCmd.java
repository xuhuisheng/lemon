package com.mossle.bpm.cmd;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.IdentityLinkType;

public class DeleteTaskWithCommentCmd implements Command<Object> {
    private String taskId;
    private String comment;

    public DeleteTaskWithCommentCmd(String taskId, String comment) {
        this.taskId = taskId;
        this.comment = comment;
    }

    public Object execute(CommandContext commandContext) {
        TaskEntity taskEntity = commandContext.getTaskEntityManager()
                .findTaskById(taskId);

        // taskEntity.fireEvent(TaskListener.EVENTNAME_COMPLETE);
        if ((Authentication.getAuthenticatedUserId() != null)
                && (taskEntity.getProcessInstanceId() != null)) {
            taskEntity.getProcessInstance().involveUser(
                    Authentication.getAuthenticatedUserId(),
                    IdentityLinkType.PARTICIPANT);
        }

        Context.getCommandContext().getTaskEntityManager()
                .deleteTask(taskEntity, comment, false);

        if (taskEntity.getExecutionId() != null) {
            ExecutionEntity execution = taskEntity.getExecution();
            execution.removeTask(taskEntity);

            // execution.signal(null, null);
        }

        return null;
    }
}
