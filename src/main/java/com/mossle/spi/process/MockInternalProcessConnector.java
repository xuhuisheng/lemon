package com.mossle.spi.process;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mossle.api.form.FormDTO;

public class MockInternalProcessConnector implements InternalProcessConnector {
    public FormDTO findTaskForm(String taskId) {
        return new FormDTO();
    }

    public List<ProcessTaskDefinition> findTaskDefinitions(
            String processDefinitionId) {
        return Collections.emptyList();
    }

    public void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssignees) {
    }

    public void completeTask(String taskId, String userId,
            Map<String, Object> variables) {
    }

    public void transfer(String taskId, String assignee, String owner) {
    }

    public void withdrawTask(String taskId) {
    }

    public void rollback(String taskId, String activityId, String userId) {
    }

    public void rollbackAuto(String taskId, String activityId) {
    }

    public void delegateTask(String taskId, String userId) {
    }

    public void resolveTask(String taskId) {
    }
}
