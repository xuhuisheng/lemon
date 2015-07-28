package com.mossle.spi.process;

import java.util.List;
import java.util.Map;

import com.mossle.api.form.FormDTO;

public interface InternalProcessConnector {
    FormDTO findTaskForm(String taskId);

    List<ProcessTaskDefinition> findTaskDefinitions(String processDefinitionId);

    void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssignees);

    void completeTask(String taskId, String userId,
            Map<String, Object> variables);

    void transfer(String taskId, String assignee, String owner);

    void withdrawTask(String taskId);

    void rollback(String taskId, String activityId, String userId);

    void rollbackAuto(String taskId, String activityId);

    void delegateTask(String taskId, String userId);

    void resolveTask(String taskId);
}
