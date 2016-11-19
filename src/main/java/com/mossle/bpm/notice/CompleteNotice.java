package com.mossle.bpm.notice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.notification.NotificationConnector;
import com.mossle.api.notification.NotificationDTO;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.bpm.persistence.domain.BpmConfNotice;
import com.mossle.bpm.persistence.manager.BpmConfNoticeManager;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
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
        NotificationConnector notificationConnector = ApplicationContextHelper
                .getBean(NotificationConnector.class);

        //
        Map<String, Object> data = new HashMap<String, Object>();
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(delegateTask.getId());
        taskEntity.setName(delegateTask.getName());
        taskEntity.setAssigneeWithoutCascade(userConnector.findById(
                delegateTask.getAssignee()).getDisplayName());
        taskEntity.setVariableLocal("initiator",
                getInitiator(userConnector, delegateTask));

        //
        data.put("task", taskEntity);
        data.put("initiator", this.getInitiator(userConnector, delegateTask));

        String receiver = bpmConfNotice.getReceiver();

        /*
         * BpmMailTemplate bpmMailTemplate = bpmConfNotice.getBpmMailTemplate(); ExpressionManager expressionManager =
         * Context .getProcessEngineConfiguration().getExpressionManager();
         */
        UserDTO userDto = null;

        if ("任务接收人".equals(receiver)) {
            userDto = userConnector.findById(delegateTask.getAssignee());
        } else if ("流程发起人".equals(receiver)) {
            userDto = userConnector.findById((String) delegateTask
                    .getVariables().get("initiator"));
        } else {
            HistoricProcessInstanceEntity historicProcessInstanceEntity = Context
                    .getCommandContext()
                    .getHistoricProcessInstanceEntityManager()
                    .findHistoricProcessInstance(
                            delegateTask.getProcessInstanceId());
            userDto = userConnector.findById(historicProcessInstanceEntity
                    .getStartUserId());
        }

        /*
         * String subject = expressionManager .createExpression(bpmMailTemplate.getSubject())
         * .getValue(taskEntity).toString();
         * 
         * String content = expressionManager .createExpression(bpmMailTemplate.getContent())
         * .getValue(taskEntity).toString(); this.sendMail(userDto, subject, content); this.sendSiteMessage(userDto,
         * subject, content);
         */
        NotificationDTO notificationDto = new NotificationDTO();
        notificationDto.setReceiver(userDto.getId());
        notificationDto.setReceiverType("userid");
        notificationDto.setTypes(Arrays.asList(bpmConfNotice
                .getNotificationType().split(",")));
        notificationDto.setData(data);
        notificationDto.setTemplate(bpmConfNotice.getTemplateCode());
        notificationConnector.send(notificationDto, delegateTask.getTenantId());
    }

    public String getInitiator(UserConnector userConnector,
            DelegateTask delegateTask) {
        return userConnector.findById(
                (String) delegateTask.getVariables().get("initiator"))
                .getDisplayName();
    }
}
