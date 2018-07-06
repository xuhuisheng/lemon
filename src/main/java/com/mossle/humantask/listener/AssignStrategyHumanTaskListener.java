package com.mossle.humantask.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.domain.TaskParticipant;
import com.mossle.humantask.persistence.manager.TaskInfoManager;

import com.mossle.spi.humantask.TaskDefinitionConnector;
import com.mossle.spi.humantask.TaskUserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssignStrategyHumanTaskListener implements HumanTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(AssignStrategyHumanTaskListener.class);
    private TaskDefinitionConnector taskDefinitionConnector;
    private TaskInfoManager taskInfoManager;

    public void onCreate(TaskInfo taskInfo) {
        if (taskInfo.getAssignee() != null) {
            return;
        }

        List<TaskParticipant> taskParticipants = new ArrayList<TaskParticipant>();

        String taskDefinitionKey = taskInfo.getCode();
        logger.debug("taskDefinitionKey : {}", taskDefinitionKey);

        String processDefinitionId = taskInfo.getProcessDefinitionId();
        logger.debug("processDefinitionId : {}", processDefinitionId);

        String strategy = taskDefinitionConnector.findTaskAssignStrategy(
                taskDefinitionKey, processDefinitionId);
        logger.debug("strategy : {}", strategy);

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
                taskParticipants.add(taskParticipant);
            }
        }

        if (strategy == null) {
            return;
        }

        if ("无".equals(strategy)) {
            return;
        }

        if ("当只有一人时采用独占策略".equals(strategy)) {
            if (taskParticipants.size() != 1) {
                logger.info("candidateUsers size is {}",
                        taskParticipants.size());

                return;
            }

            String userId = taskParticipants.get(0).getId().toString();

            logger.info("assign : {}", userId);
            taskInfo.setAssignee(userId);
        } else if ("资源中任务最少者".equals(strategy)) {
            if (taskParticipants.isEmpty()) {
                logger.info("candidateUsers is empty");

                return;
            }

            String userId = taskParticipants.get(0).getRef();
            int taskCount = 0;

            for (TaskParticipant candidateUser : taskParticipants) {
                int currentTaskCount = taskInfoManager
                        .getCount(
                                "select count(*) from TaskInfo where assignee=? and status='active'",
                                candidateUser.getRef());

                if ((taskCount == 0) || (currentTaskCount < taskCount)) {
                    taskCount = currentTaskCount;
                    userId = candidateUser.getRef();
                }
            }

            logger.info("taskCount : {}", taskCount);

            logger.info("assign : {}", userId);
            taskInfo.setAssignee(userId);
        } else if ("资源中随机分配".equals(strategy)) {
            if (taskParticipants.isEmpty()) {
                logger.info("candidateUsers is empty");

                return;
            }

            Collections.shuffle(taskParticipants);

            String userId = taskParticipants.get(0).getRef();

            logger.info("assign : {}", userId);
            taskInfo.setAssignee(userId);
        } else {
            logger.warn("unsupport strategy : {}", strategy);
        }
    }

    @Override
    public void onComplete(TaskInfo taskInfo) throws Exception {
    }

    public List<String> findCandidateUsers(TaskInfo taskInfo) {
        List<String> candidateUsers = new ArrayList<String>();
        Set<TaskParticipant> taskParticipants = taskInfo.getTaskParticipants();

        for (TaskParticipant taskParticipant : taskParticipants) {
            if (!"user".equals(taskParticipant.getType())) {
                logger.info("unsupport type : {}", taskParticipant.getType());

                continue;
            }

            candidateUsers.add(taskParticipant.getRef());
        }

        return candidateUsers;
    }

    @Resource
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }

    @Resource
    public void setTaskInfoManager(TaskInfoManager taskInfoManager) {
        this.taskInfoManager = taskInfoManager;
    }
}
