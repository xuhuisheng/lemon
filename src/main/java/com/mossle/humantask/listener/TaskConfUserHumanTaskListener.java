package com.mossle.humantask.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.domain.TaskParticipant;
import com.mossle.humantask.persistence.manager.TaskParticipantManager;

import com.mossle.spi.humantask.TaskDefinitionConnector;
import com.mossle.spi.humantask.TaskUserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskConfUserHumanTaskListener implements HumanTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(TaskConfUserHumanTaskListener.class);
    private TaskDefinitionConnector taskDefinitionConnector;
    private TaskParticipantManager taskParticipantManager;

    @Override
    public void onCreate(TaskInfo taskInfo) throws Exception {
        String taskDefinitionKey = taskInfo.getCode();
        String businessKey = taskInfo.getBusinessKey();
        String assignee = taskDefinitionConnector.findTaskConfUser(
                taskDefinitionKey, businessKey);

        if (assignee != null) {
            taskInfo.setAssignee(assignee);
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
    public void setTaskParticipantManager(
            TaskParticipantManager taskParticipantManager) {
        this.taskParticipantManager = taskParticipantManager;
    }
}
