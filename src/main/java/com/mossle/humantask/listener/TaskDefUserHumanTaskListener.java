package com.mossle.humantask.listener;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.humantask.HumanTaskConstants;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.domain.TaskParticipant;
import com.mossle.humantask.persistence.manager.TaskParticipantManager;

import com.mossle.spi.humantask.TaskDefinitionConnector;
import com.mossle.spi.humantask.TaskUserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskDefUserHumanTaskListener implements HumanTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(TaskDefUserHumanTaskListener.class);
    private TaskDefinitionConnector taskDefinitionConnector;
    private TaskParticipantManager taskParticipantManager;

    @Override
    public void onCreate(TaskInfo taskInfo) throws Exception {
        if (HumanTaskConstants.CATALOG_COPY.equals(taskInfo.getCatalog())) {
            return;
        }

        String taskDefinitionKey = taskInfo.getCode();
        String processDefinitionId = taskInfo.getProcessDefinitionId();
        List<TaskUserDTO> taskUsers = taskDefinitionConnector.findTaskUsers(
                taskDefinitionKey, processDefinitionId);

        for (TaskUserDTO taskUser : taskUsers) {
            String catalog = taskUser.getCatalog();
            String type = taskUser.getType();
            String value = taskUser.getValue();

            if ("assignee".equals(catalog)) {
                taskInfo.setAssignee(value);
            } else if ("candidate".equals(catalog)) {
                TaskParticipant taskParticipant = new TaskParticipant();
                taskParticipant.setCategory(catalog);
                taskParticipant.setRef(value);
                taskParticipant.setType(type);
                taskParticipant.setTaskInfo(taskInfo);
                taskParticipantManager.save(taskParticipant);
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
    public void setTaskParticipantManager(
            TaskParticipantManager taskParticipantManager) {
        this.taskParticipantManager = taskParticipantManager;
    }
}
