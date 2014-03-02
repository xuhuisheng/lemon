package com.mossle.bpm.cmd;

import com.mossle.api.user.UserConnector;

import com.mossle.bpm.notice.TimeoutNotice;

import com.mossle.core.spring.ApplicationContextHelper;

import com.mossle.ext.mail.MailFacade;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

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
