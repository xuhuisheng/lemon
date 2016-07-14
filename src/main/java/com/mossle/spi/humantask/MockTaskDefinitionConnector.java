package com.mossle.spi.humantask;

import java.util.List;

public class MockTaskDefinitionConnector implements TaskDefinitionConnector {
    public String findTaskAssignStrategy(String taskDefinitionKey,
            String processDefinitionId) {
        return null;
    }

    public CounterSignDTO findCounterSign(String taskDefinitionKey,
            String processDefinitionId) {
        return null;
    }

    public FormDTO findForm(String taskDefinitionKey, String processDefinitionId) {
        return null;
    }

    public List<String> findOperations(String taskDefinitionKey,
            String processDefinitionId) {
        return null;
    }

    public List<TaskUserDTO> findTaskUsers(String taskDefinitionKey,
            String processDefinitionId) {
        return null;
    }

    public List<DeadlineDTO> findDeadlines(String taskDefinitionKey,
            String processDefinitionId) {
        return null;
    }

    public String findTaskConfUser(String taskDefinitionKey, String businessKey) {
        return null;
    }

    public List<TaskNotificationDTO> findTaskNotifications(
            String taskDefinitionKey, String processDefinitionId,
            String eventName) {
        return null;
    }

    public void create(TaskDefinitionDTO taskDefinition) {
    }

    public void saveAssignStrategy(String taskDefinitionKey,
            String processDefinitoinId, String assigneeStrategy) {
    }

    public void saveCounterSign(String taskDefinitionKey,
            String processDefinitionId, CounterSignDTO counterSign) {
    }

    public void saveForm(String taskDefinitionKey, String processDefinitionId,
            FormDTO form) {
    }

    public void addOperation(String taskDefinitionKey,
            String processDefinitionId, String operation) {
    }

    public void removeOperation(String taskDefinitionKey,
            String processDefinitionId, String operation) {
    }

    public void addTaskUser(String taskDefinitionKey,
            String processDefinitionId, TaskUserDTO taskUser) {
    }

    public void removeTaskUser(String taskDefinitionKey,
            String processDefinitionId, TaskUserDTO taskUser) {
    }

    public void updateTaskUser(String taskDefinitionKey,
            String processDefinitionId, TaskUserDTO taskUser, String status) {
    }

    public void addTaskNotification(String taskDefinitionKey,
            String processDefinitionId, TaskNotificationDTO taskNotification) {
    }

    public void removeTaskNotification(String taskDefinitionKey,
            String processDefinitionId, TaskNotificationDTO taskNotification) {
    }

    public void addDeadline(String taskDefinitionKey,
            String processDefinitionId, DeadlineDTO deadline) {
    }

    public void removeDeadline(String taskDefinitionKey,
            String processDefinitionId, DeadlineDTO deadline) {
    }
}
