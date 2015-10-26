package com.mossle.humantask.listener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.mossle.humantask.persistence.domain.TaskDeadline;
import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.manager.TaskDeadlineManager;

import com.mossle.spi.humantask.DeadlineDTO;
import com.mossle.spi.humantask.TaskDefinitionConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeadlineHumanTaskListener implements HumanTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(DeadlineHumanTaskListener.class);
    private TaskDefinitionConnector taskDefinitionConnector;
    private TaskDeadlineManager taskDeadlineManager;

    @Override
    public void onCreate(TaskInfo taskInfo) {
        String taskDefinitionKey = taskInfo.getCode();

        String processDefinitionId = taskInfo.getProcessDefinitionId();
        List<DeadlineDTO> deadlines = taskDefinitionConnector.findDeadlines(
                taskDefinitionKey, processDefinitionId);

        for (DeadlineDTO deadline : deadlines) {
            try {
                String durationText = deadline.getDuration();

                Date now = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);

                DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
                Duration duration = datatypeFactory.newDuration(durationText);
                duration.addTo(calendar);

                Date deadlineTime = calendar.getTime();
                TaskDeadline taskDeadline = new TaskDeadline();
                taskDeadline.setTaskInfo(taskInfo);
                taskDeadline.setType(deadline.getType());
                taskDeadline.setDeadlineTime(deadlineTime);
                taskDeadline
                        .setNotificationType(deadline.getNotificationType());
                taskDeadline.setNotificationTemplateCode(deadline
                        .getNotificationTemplateCode());
                taskDeadline.setNotificationReceiver(deadline
                        .getNotificationReceiver());
                taskDeadlineManager.save(taskDeadline);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void onComplete(TaskInfo taskInfo) throws Exception {
    }

    @Resource
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }

    @Resource
    public void setTaskDeadlineManager(TaskDeadlineManager taskDeadlineManager) {
        this.taskDeadlineManager = taskDeadlineManager;
    }
}
