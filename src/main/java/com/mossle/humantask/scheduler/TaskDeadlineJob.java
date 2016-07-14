package com.mossle.humantask.scheduler;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.notification.NotificationConnector;
import com.mossle.api.notification.NotificationDTO;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.humantask.persistence.domain.TaskDeadline;
import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.manager.TaskDeadlineManager;

import com.mossle.spi.process.InternalProcessConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
public class TaskDeadlineJob {
    private static Logger logger = LoggerFactory
            .getLogger(TaskDeadlineJob.class);
    private TaskDeadlineManager taskDeadlineManager;
    private NotificationConnector notificationConnector;
    private UserConnector userConnector;
    private InternalProcessConnector internalProcessConnector;
    private boolean active;
    private String baseUrl;

    @Scheduled(cron = "0/10 * * * * ?")
    @Transactional
    public void execute() throws Exception {
        if (!active) {
            return;
        }

        logger.debug("start");

        String hql = "from TaskDeadline";
        List<TaskDeadline> taskDeadlines = taskDeadlineManager.find(hql);

        Date now = new Date();

        for (TaskDeadline taskDeadline : taskDeadlines) {
            if (now.after(taskDeadline.getDeadlineTime())) {
                String type = taskDeadline.getNotificationType();
                String receiver = taskDeadline.getNotificationReceiver();
                String templateCode = taskDeadline
                        .getNotificationTemplateCode();

                this.doNotice(taskDeadline.getTaskInfo(), type, receiver,
                        templateCode);
            }
        }

        logger.debug("end");
    }

    public void doNotice(TaskInfo taskInfo, String type, String receiver,
            String templateCode) {
        String taskDefinitionKey = taskInfo.getCode();
        String processDefinitionId = taskInfo.getProcessDefinitionId();

        Map<String, Object> data = this.prepareData(taskInfo);

        logger.debug("receiver : {}", receiver);

        UserDTO userDto = null;

        if ("任务接收人".equals(receiver)) {
            userDto = userConnector.findById(taskInfo.getAssignee());
        } else if ("流程发起人".equals(receiver)) {
            String initiator = internalProcessConnector.findInitiator(taskInfo
                    .getProcessInstanceId());
            userDto = userConnector.findById(initiator);
        } else {
            userDto = userConnector.findById(receiver);
        }

        NotificationDTO notificationDto = new NotificationDTO();
        notificationDto.setReceiver(userDto.getId());
        notificationDto.setReceiverType("userid");
        notificationDto.setTypes(Arrays.asList(type.split(",")));
        notificationDto.setData(data);
        notificationDto.setTemplate(templateCode);
        notificationConnector.send(notificationDto, taskInfo.getTenantId());
    }

    public Map<String, Object> prepareData(TaskInfo taskInfo) {
        String assignee = taskInfo.getAssignee();
        String initiator = internalProcessConnector.findInitiator(taskInfo
                .getProcessInstanceId());
        UserDTO assigneeUser = userConnector.findById(assignee);
        UserDTO initiatorUser = userConnector.findById(initiator);

        Map<String, Object> data = new HashMap<String, Object>();

        Map<String, Object> taskEntity = new HashMap<String, Object>();
        taskEntity.put("id", taskInfo.getId());
        taskEntity.put("name", taskInfo.getName());
        taskEntity.put("assignee", assigneeUser.getDisplayName());

        data.put("task", taskEntity);
        data.put("initiator", initiatorUser.getDisplayName());
        data.put("humanTask", taskInfo);
        data.put("baseUrl", baseUrl);
        data.put("humanTaskId", Long.toString(taskInfo.getId()));

        return data;
    }

    @Resource
    public void setTaskDeadlineManager(TaskDeadlineManager taskDeadlineManager) {
        this.taskDeadlineManager = taskDeadlineManager;
    }

    @Resource
    public void setNotificationConnector(
            NotificationConnector notificationConnector) {
        this.notificationConnector = notificationConnector;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setInternalProcessConnector(
            InternalProcessConnector internalProcessConnector) {
        this.internalProcessConnector = internalProcessConnector;
    }

    @Value("${humantask.schedule.deadline.active}")
    public void setActive(boolean active) {
        this.active = active;
    }

    @Value("${application.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
