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

    /**
     * 保存任务，同时处理参与者.
     */
    HumanTaskDTO saveHumanTaskAndProcess(HumanTaskDTO humanTaskDto);

    /**
     * 完成任务.
     */
    void completeTask(String humanTaskId, String userId,
            Map<String, Object> taskParameters);

    /**
     * 领取任务.
     */
    void claimTask(String humanTaskId, String userId);

    /**
     * 释放任务。
     */
    void releaseTask(String humanTaskId);

    /**
     * 转发任务.
     */
    void transfer(String humanTaskId, String userId);

    /**
     * 回退，指定节点，重新分配.
     */
    void rollbackActivity(String humanTaskId, String activityId);

    /**
     * 回退，指定节点，上个执行人.
     */
    void rollbackActivityLast(String humanTaskId, String activityId);

    /**
     * 回退，指定节点，指定执行人.
     */
    void rollbackActivityAssignee(String humanTaskId, String activityId,
            String userId);

    /**
     * 回退，上个节点，重新分配.
     */
    void rollbackPrevious(String humanTaskId);

    /**
     * 回退，上个节点，上个执行人.
     */
    void rollbackPreviousLast(String humanTaskId);

    /**
     * 回退，上个节点，指定执行人.
     */
    void rollbackPreviousAssignee(String humanTaskId, String userId);

    /**
     * 回退，开始事件，流程发起人.
     */
    void rollbackStart(String humanTaskId);

    /**
     * 撤销.
     */
    void withdraw(String humanTaskId);

    /**
     * 协办.
     */
    void delegateTask(String humanTaskId, String userId);

    /**
     * 协办，链状.
     */
    void delegateTaskCreate(String humanTaskId, String userId);

    void saveParticipant(ParticipantDTO participantDto);

    HumanTaskDTO findHumanTaskByTaskId(String taskId);

    HumanTaskDTO findHumanTask(String humanTaskId);

    FormDTO findTaskForm(String humanTaskId);

    List<HumanTaskDefinition> findHumanTaskDefinitions(
            String processDefinitionId);

    void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssigness);

    Page findPersonalTasks(String userId, int pageNo, int pageSize);

    Page findFinishedTasks(String userId, int pageNo, int pageSize);
}
