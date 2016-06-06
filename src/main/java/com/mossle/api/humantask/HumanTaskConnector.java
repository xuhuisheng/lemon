package com.mossle.api.humantask;

import java.util.List;
import java.util.Map;

import com.mossle.api.form.FormDTO;

import com.mossle.core.page.Page;

public interface HumanTaskConnector {
    /**
     * 创建任务.
     */
    HumanTaskDTO createHumanTask();

    /**
     * 删除任务.
     */
    void removeHumanTask(String humanTaskId);

    void removeHumanTaskByTaskId(String taskId);

    void removeHumanTaskByProcessInstanceId(String processInstanceId);

    /**
     * 更新任务.
     */
    HumanTaskDTO saveHumanTask(HumanTaskDTO humanTaskDto);

    HumanTaskDTO saveHumanTask(HumanTaskDTO humanTaskDto,
            boolean triggerListener);

    /**
     * 保存任务，同时处理参与者.
     */
    HumanTaskDTO saveHumanTaskAndProcess(HumanTaskDTO humanTaskDto);

    /**
     * 完成任务.
     */
    void completeTask(String humanTaskId, String userId, String action,
            String comment, Map<String, Object> taskParameters);

    /**
     * 领取任务.
     */
    void claimTask(String humanTaskId, String userId);

    /**
     * 释放任务。
     */
    void releaseTask(String humanTaskId, String comment);

    /**
     * 转发任务.
     */
    void transfer(String humanTaskId, String userId, String comment);

    /**
     * 取消转办.
     */
    void cancel(String humanTaskId, String userId, String comment);

    /**
     * 回退，指定节点，重新分配.
     */
    void rollbackActivity(String humanTaskId, String activityId, String comment);

    /**
     * 回退，指定节点，上个执行人.
     */
    void rollbackActivityLast(String humanTaskId, String activityId,
            String comment);

    /**
     * 回退，指定节点，指定执行人.
     */
    void rollbackActivityAssignee(String humanTaskId, String activityId,
            String userId, String comment);

    /**
     * 回退，上个节点，重新分配.
     */
    void rollbackPrevious(String humanTaskId, String comment);

    /**
     * 回退，上个节点，上个执行人.
     */
    void rollbackPreviousLast(String humanTaskId, String comment);

    /**
     * 回退，上个节点，指定执行人.
     */
    void rollbackPreviousAssignee(String humanTaskId, String userId,
            String comment);

    /**
     * 回退，开始事件，流程发起人.
     */
    void rollbackStart(String humanTaskId, String comment);

    /**
     * 回退，流程发起人.
     */
    void rollbackInitiator(String humanTaskId, String comment);

    /**
     * 撤销.
     */
    void withdraw(String humanTaskId, String comment);

    /**
     * 协办.
     */
    void delegateTask(String humanTaskId, String userId, String comment);

    /**
     * 协办，链状.
     */
    void delegateTaskCreate(String humanTaskId, String userId, String comment);

    /**
     * 沟通.
     */
    void communicate(String humanTaskId, String userId, String comment);

    /**
     * 反馈.
     */
    void callback(String humanTaskId, String userId, String comment);

    /**
     * 跳过.
     */
    void skip(String humanTaskId, String userId, String comment);

    void saveParticipant(ParticipantDTO participantDto);

    HumanTaskDTO findHumanTaskByTaskId(String taskId);

    List<HumanTaskDTO> findHumanTasksByProcessInstanceId(
            String processInstanceId);

    HumanTaskDTO findHumanTask(String humanTaskId);

    List<HumanTaskDTO> findSubTasks(String parentTaskId);

    FormDTO findTaskForm(String humanTaskId);

    List<HumanTaskDefinition> findHumanTaskDefinitions(
            String processDefinitionId);

    void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssigness);

    /** 待办任务. */
    Page findPersonalTasks(String userId, String tenantId, int pageNo,
            int pageSize);

    /** 已办任务. */
    Page findFinishedTasks(String userId, String tenantId, int pageNo,
            int pageSize);

    /** 待领任务. */
    Page findGroupTasks(String userId, String tenantId, int pageNo, int pageSize);

    /** 经手任务. */
    Page findDelegateTasks(String userId, String tenantId, int pageNo,
            int pageSize);
}
