package com.mossle.bpm.notice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.notification.NotificationConnector;
import com.mossle.api.notification.NotificationDTO;
import com.mossle.client.user.UserClient;
import com.mossle.api.user.UserDTO;

import com.mossle.bpm.persistence.domain.BpmConfNotice;
import com.mossle.bpm.persistence.manager.BpmConfNoticeManager;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrivalNotice {
    private static Logger logger = LoggerFactory.getLogger(ArrivalNotice.class);
    public static final int TYPE_ARRIVAL = 0;
    public static final int TYPE_COMPLETE = 1;
    public static final int TYPE_TIMEOUT = 2;

    public void process(DelegateTask delegateTask) {
        if (delegateTask.getAssignee() == null) {
            return;
        }

        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String processDefinitionId = delegateTask.getProcessDefinitionId();

        List<BpmConfNotice> bpmConfNotices = ApplicationContextHelper
                .getBean(BpmConfNoticeManager.class)
                .find("from BpmConfNotice where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, taskDefinitionKey);

        for (BpmConfNotice bpmConfNotice : bpmConfNotices) {
            if (TYPE_ARRIVAL == bpmConfNotice.getType()) {
                processArrival(delegateTask, bpmConfNotice);
            }
        }
    }

    public void processArrival(DelegateTask delegateTask,
            BpmConfNotice bpmConfNotice) {
        UserClient userClient = ApplicationContextHelper
                .getBean(UserClient.class);
        NotificationConnector notificationConnector = ApplicationContextHelper
                .getBean(NotificationConnector.class);

        //
        Map<String, Object> data = new HashMap<String, Object>();

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(delegateTask.getId());
        taskEntity.setName(delegateTask.getName());
        taskEntity.setAssigneeWithoutCascade(userClient.findById(
                delegateTask.getAssignee(), "1").getDisplayName());
        taskEntity.setVariableLocal("initiator",
                getInitiator(userClient, delegateTask));
        logger.debug("initiator : {}", delegateTask.getVariable("initator"));
        logger.debug("variables : {}", delegateTask.getVariables());
        //
        data.put("task", taskEntity);
        data.put("initiator", this.getInitiator(userClient, delegateTask));

        String receiver = bpmConfNotice.getReceiver();

        /*
         * BpmMailTemplate bpmMailTemplate = bpmConfNotice.getBpmMailTemplate(); ExpressionManager expressionManager =
         * Context .getProcessEngineConfiguration().getExpressionManager();
         */
        UserDTO userDto = null;

        /*
         * String subject = expressionManager .createExpression(bpmMailTemplate.getSubject())
         * .getValue(taskEntity).toString();
         * 
         * String content = expressionManager .createExpression(bpmMailTemplate.getContent())
         * .getValue(taskEntity).toString();
         */
        if ("任务接收人".equals(receiver)) {
            userDto = userClient.findById(delegateTask.getAssignee(), "1");
        } else if ("流程发起人".equals(receiver)) {
            userDto = userClient.findById((String) delegateTask.getVariables()
                    .get("initiator"), "1");
        } else {
            HistoricProcessInstanceEntity historicProcessInstanceEntity = Context
                    .getCommandContext()
                    .getHistoricProcessInstanceEntityManager()
                    .findHistoricProcessInstance(
                            delegateTask.getProcessInstanceId());
            userDto = userClient.findById(
                    historicProcessInstanceEntity.getStartUserId(), "1");
        }

        // this.sendMail(userDto, subject, content);
        // this.sendSiteMessage(userDto, subject, content);
        NotificationDTO notificationDto = new NotificationDTO();
        notificationDto.setReceiver(userDto.getId());
        notificationDto.setReceiverType("userid");
        notificationDto.setTypes(Arrays.asList(bpmConfNotice
                .getNotificationType().split(",")));
        notificationDto.setData(data);
        notificationDto.setTemplate(bpmConfNotice.getTemplateCode());
        notificationConnector.send(notificationDto, delegateTask.getTenantId());
    }

    public String getInitiator(UserClient userClient, DelegateTask delegateTask) {
        return userClient.findById(
                (String) delegateTask.getVariables().get("initiator"), "1")
                .getDisplayName();
    }
}
