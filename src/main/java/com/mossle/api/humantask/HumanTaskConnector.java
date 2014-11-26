package com.mossle.api.humantask;

import java.util.List;
import java.util.Map;

import com.mossle.api.form.FormDTO;

public interface HumanTaskConnector {
    HumanTaskDTO findHumanTask(String taskId);

    FormDTO findTaskForm(String taskId);

    List<HumanTaskDefinition> findHumanTaskDefinitions(
            String processDefinitionId);

    void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssigness);

    void completeTask(String taskId, String userId,
            Map<String, Object> taskParameters);
}
