package com.mossle.bpm.notice;

import java.util.List;

import com.mossle.api.user.UserConnector;

import com.mossle.bpm.persistence.domain.BpmConfNotice;
import com.mossle.bpm.persistence.domain.BpmMailTemplate;
import com.mossle.bpm.persistence.manager.BpmConfNoticeManager;

import com.mossle.core.spring.ApplicationContextHelper;

import com.mossle.ext.mail.MailFacade;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

public class CompleteNotice {
    public static final int TYPE_ARRIVAL = 0;
    public static final int TYPE_COMPLETE = 1;
    public static final int TYPE_TIMEOUT = 2;

    public void process(DelegateTask delegateTask) {
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String processDefinitionId = delegateTask.getProcessDefinitionId();

        List<BpmConfNotice> bpmConfNotices = ApplicationContextHelper
                .getBean(BpmConfNoticeManager.class)
                .find("from BpmConfNotice where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, taskDefinitionKey);

        for (BpmConfNotice bpmConfNotice : bpmConfNotices) {
            if (TYPE_COMPLETE == bpmConfNotice.getType()) {
                processComplete(delegateTask, bpmConfNotice);
            }
        }
    }

    public void processComplete(DelegateTask delegateTask,
            BpmConfNotice bpmConfNotice) {
        UserConnector userConnector = ApplicationContextHelper
                .getBean(UserConnector.class);
        MailFacade mailFacade = ApplicationContextHelper
                .getBean(MailFacade.class);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(delegateTask.getId());
        taskEntity.setName(delegateTask.getName());
        taskEntity.setAssigneeWithoutCascade(userConnector.findById(
                delegateTask.getAssignee()).getDisplayName());
        taskEntity.setVariableLocal("initiator",
                getInitiator(userConnector, delegateTask));

        String receiver = bpmConfNotice.getReceiver();
        BpmMailTemplate bpmMailTemplate = bpmConfNotice.getBpmMailTemplate();
        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();
        String to = null;

        if ("任务接收人".equals(receiver)) {
            to = userConnector.findById(delegateTask.getAssignee()).getEmail();
        } else if ("流程发起人".equals(receiver)) {
            to = userConnector.findById(
                    (String) delegateTask.getVariables().get("initiator"))
                    .getEmail();
        } else {
            HistoricProcessInstanceEntity historicProcessInstanceEntity = Context
                    .getCommandContext()
                    .getHistoricProcessInstanceEntityManager()
                    .findHistoricProcessInstance(
                            delegateTask.getProcessInstanceId());
            to = userConnector.findById(
                    historicProcessInstanceEntity.getStartUserId()).getEmail();
        }

        String subject = expressionManager
                .createExpression(bpmMailTemplate.getSubject())
                .getValue(taskEntity).toString();

        String content = expressionManager
                .createExpression(bpmMailTemplate.getContent())
                .getValue(taskEntity).toString();
        mailFacade.sendMail(to, subject, content);
    }

    public String getInitiator(UserConnector userConnector,
            DelegateTask delegateTask) {
        return userConnector.findById(
                (String) delegateTask.getVariables().get("initiator"))
                .getDisplayName();
    }
}
