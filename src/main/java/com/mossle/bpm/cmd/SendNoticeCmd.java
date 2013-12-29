package com.mossle.bpm.cmd;

import com.mossle.core.spring.ApplicationContextHolder;

import com.mossle.ext.mail.MailFacade;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

public class SendNoticeCmd implements Command<Object> {
    private String taskId;
    private String receiver;
    private String subject;
    private String content;

    public SendNoticeCmd(String taskId, String receiver, String subject,
            String content) {
        this.taskId = taskId;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
    }

    public Object execute(CommandContext commandContext) {
        TaskEntity taskEntity = commandContext.getTaskEntityManager()
                .findTaskById(taskId);

        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();

        String email = taskEntity.getAssignee() + "@gmail.com";
        String targetSubject = expressionManager.createExpression(subject)
                .getValue(taskEntity).toString();

        String targetContent = expressionManager.createExpression(content)
                .getValue(taskEntity).toString();
        ApplicationContextHolder
                .getInstance()
                .getApplicationContext()
                .getBean(MailFacade.class)
                .sendMail("no-reply@lemon.mossle.com", email, targetSubject,
                        targetContent);

        return null;
    }
}
