package com.mossle.api.humantask;

import java.util.List;
import java.util.Map;

import com.mossle.api.form.FormDTO;

import com.mossle.core.page.Page;

public class MockHumanTaskConnector implements HumanTaskConnector {
    public HumanTaskDTO createHumanTask() {
        return null;
    }

    public void removeHumanTask(String humanTaskId) {
    }

    public void removeHumanTaskByTaskId(String taskId) {
    }

    public void removeHumanTaskByProcessInstanceId(String processInstanceId) {
    }

    public HumanTaskDTO saveHumanTask(HumanTaskDTO humanTaskDto) {
        return null;
    }

    public HumanTaskDTO saveHumanTask(HumanTaskDTO humanTaskDto,
            boolean triggerListener) {
        return null;
    }

    public HumanTaskDTO saveHumanTaskAndProcess(HumanTaskDTO humanTaskDto) {
        return null;
    }

    public HumanTaskDTO findHumanTaskByTaskId(String taskId) {
        return null;
    }

    public List<HumanTaskDTO> findHumanTasksByProcessInstanceId(
            String processInstanceId) {
        return null;
    }

    public List<HumanTaskDTO> findSubTasks(String parentTaskId) {
        return null;
    }

    public HumanTaskDTO findHumanTask(String humanTaskId) {
        return null;
    }

    public FormDTO findTaskForm(String humanTaskId) {
        return null;
    }

    public List<HumanTaskDefinition> findHumanTaskDefinitions(
            String processDefinitionId) {
        return null;
    }

    public void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssigness) {
    }

    public void completeTask(String humanTaskId, String userId, String action,
            String comment, Map<String, Object> taskParameters) {
    }

    public Page findPersonalTasks(String userId, String tenantId, int pageNo,
            int pageSize) {
        return null;
    }

    public Page findFinishedTasks(String userId, String tenantId, int pageNo,
            int pageSize) {
        return null;
    }

    public Page findGroupTasks(String userId, String tenantId, int pageNo,
            int pageSize) {
        return null;
    }

    public Page findDelegateTasks(String userId, String tenantId, int pageNo,
            int pageSize) {
        return null;
    }

    public void claimTask(String humanTaskId, String userId) {
    }

    public void releaseTask(String humanTaskId, String comment) {
    }

    public void transfer(String humanTaskId, String userId, String comment) {
    }

    public void cancel(String humanTaskId, String userId, String comment) {
    }

    /**
     * 回退，指定节点，重新分配.
     */
    public void rollbackActivity(String humanTaskId, String activityId,
            String comment) {
    }

    /**
     * 回退，指定节点，上个执行人.
     */
    public void rollbackActivityLast(String humanTaskId, String activityId,
            String comment) {
    }

    /**
     * 回退，指定节点，指定执行人.
     */
    public void rollbackActivityAssignee(String humanTaskId, String activityId,
            String userId, String comment) {
    }

    /**
     * 回退，上个节点，重新分配.
     */
    public void rollbackPrevious(String humanTaskId, String comment) {
    }

    /**
     * 回退，上个节点，上个执行人.
     */
    public void rollbackPreviousLast(String humanTaskId, String comment) {
    }

    /**
     * 回退，上个节点，指定执行人.
     */
    public void rollbackPreviousAssignee(String humanTaskId, String userId,
            String comment) {
    }

    /**
     * 回退，开始事件，流程发起人.
     */
    public void rollbackStart(String humanTaskId, String comment) {
    }

    /**
     * 回退，流程发起人.
     */
    public void rollbackInitiator(String humanTaskId, String comment) {
    }

    public void withdraw(String humanTaskId, String comment) {
    }

    public void delegateTask(String humanTaskId, String userId, String comment) {
    }

    public void delegateTaskCreate(String humanTaskId, String userId,
            String comment) {
    }

    public void saveParticipant(ParticipantDTO participantDto) {
    }

    public void communicate(String humanTaskId, String userId, String comment) {
    }

    public void callback(String humanTaskId, String userId, String comment) {
    }

    public void skip(String humanTaskId, String userId, String comment) {
    }
}
