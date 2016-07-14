package com.mossle.bpm.cmd;

import com.mossle.bpm.notice.TimeoutNotice;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

public class SendNoticeCmd implements Command<Void> {
    private String taskId;

    public SendNoticeCmd(String taskId) {
        this.taskId = taskId;
    }

    public Void execute(CommandContext commandContext) {
        TaskEntity delegateTask = commandContext.getTaskEntityManager()
                .findTaskById(taskId);
        new TimeoutNotice().process(delegateTask);

        return null;
    }
}
