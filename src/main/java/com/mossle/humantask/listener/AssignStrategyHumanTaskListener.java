package com.mossle.humantask.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.domain.TaskParticipant;
import com.mossle.humantask.persistence.manager.TaskInfoManager;

import com.mossle.spi.humantask.TaskDefinitionConnector;

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

        String taskDefinitionKey = taskInfo.getCode();
        logger.debug("taskDefinitionKey : {}", taskDefinitionKey);

        String processDefinitionId = taskInfo.getProcessDefinitionId();
        logger.debug("processDefinitionId : {}", processDefinitionId);

        String strategy = taskDefinitionConnector.findTaskAssignStrategy(
                taskDefinitionKey, processDefinitionId);
        logger.debug("strategy : {}", strategy);

        if (strategy == null) {
            return;
        }

        if ("无".equals(strategy)) {
            return;
        }

        if ("当只有一人时采用独占策略".equals(strategy)) {
            List<String> candidateUsers = this.findCandidateUsers(taskInfo);

            if (candidateUsers.size() != 1) {
                logger.info("candidateUsers size is {}", candidateUsers.size());

                return;
            }

            String userId = candidateUsers.get(0);

            logger.info("assign : {}", userId);
            taskInfo.setAssignee(userId);
        } else if ("资源中任务最少者".equals(strategy)) {
            List<String> candidateUsers = this.findCandidateUsers(taskInfo);

            if (candidateUsers.isEmpty()) {
                logger.info("candidateUsers is empty");

                return;
            }

            String userId = candidateUsers.get(0);
            int taskCount = 0;

            for (String candidateUser : candidateUsers) {
                int currentTaskCount = taskInfoManager
                        .getCount(
                                "select count(*) from TaskInfo where assignee=? and status='active'",
                                candidateUser);

                if ((taskCount == 0) || (currentTaskCount < taskCount)) {
                    taskCount = currentTaskCount;
                    userId = candidateUser;
                }
            }

            logger.info("taskCount : {}", taskCount);

            logger.info("assign : {}", userId);
            taskInfo.setAssignee(userId);
        } else if ("资源中随机分配".equals(strategy)) {
            List<String> candidateUsers = this.findCandidateUsers(taskInfo);

            if (candidateUsers.isEmpty()) {
                logger.info("candidateUsers is empty");

                return;
            }

            Collections.shuffle(candidateUsers);

            String userId = candidateUsers.get(0);

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
