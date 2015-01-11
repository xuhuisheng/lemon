package com.mossle.api.humantask;

import java.util.List;
import java.util.Map;

import com.mossle.api.form.FormDTO;

import com.mossle.core.page.Page;

public interface HumanTaskConnector {
    HumanTaskDTO createHumanTask();

    HumanTaskDTO saveHumanTask(HumanTaskDTO humanTaskDto);

    HumanTaskDTO findHumanTaskByTaskId(String taskId);

    HumanTaskDTO findHumanTask(String humanTaskId);

    FormDTO findTaskForm(String humanTaskId);

    List<HumanTaskDefinition> findHumanTaskDefinitions(
            String processDefinitionId);

    void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssigness);

    void completeTask(String humanTaskId, String userId,
            Map<String, Object> taskParameters);

    Page findPersonalTasks(String userId, int pageNo, int pageSize);

    Page findFinishedTasks(String userId, int pageNo, int pageSize);

    void rollbackPrevious(String humanTaskId);

    void withdraw(String humanTaskId);

    void transfer(String humanTaskId, String userId);

    void delegateTask(String humanTaskId, String userId);
}
