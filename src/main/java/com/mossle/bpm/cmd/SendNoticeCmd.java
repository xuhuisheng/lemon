package com.mossle.bpm.cmd;

import com.mossle.core.mail.MailService;
import com.mossle.core.spring.ApplicationContextHolder;

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
    private String template;

    public SendNoticeCmd(String taskId, String receiver, String template) {
        this.taskId = taskId;
        this.receiver = receiver;
        this.template = template;
    }

    public Object execute(CommandContext commandContext) {
        TaskEntity taskEntity = commandContext.getTaskEntityManager()
                .findTaskById(taskId);

        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();

        String email = taskEntity.getAssignee() + "@gmail.com";
        String subject = "任务即将过期";

        String content = expressionManager.createExpression(template)
                .getValue(taskEntity).toString();
        ApplicationContextHolder.getInstance().getApplicationContext()
                .getBean(MailService.class).send(email, subject, content);

        return null;
    }
}
