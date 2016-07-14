package com.mossle.spi.process;

import java.util.List;
import java.util.Map;

import com.mossle.api.form.FormDTO;

/**
 * HumanTask就靠这个接口与activiti交互了.
 */
public interface InternalProcessConnector {
    /**
     * 找到任务对应的表单信息.
     */
    FormDTO findTaskForm(String taskId);

    /**
     * 找到一个流程定义下所有的任务定义.
     */
    List<ProcessTaskDefinition> findTaskDefinitions(String processDefinitionId);

    /**
     * 完成任务.
     */
    void completeTask(String taskId, String userId,
            Map<String, Object> variables);

    /**
     * 转发任务.
     */
    void transfer(String taskId, String assignee, String owner);

    /**
     * 撤销任务.
     */
    void withdrawTask(String taskId);

    /**
     * 回退任务
     */
    void rollback(String taskId, String activityId, String userId);

    /**
     * 回退任务，并自动分配最后的负责人.
     */
    void rollbackAuto(String taskId, String activityId);

    /**
     * 协办.
     */
    void delegateTask(String taskId, String userId);

    /**
     * 完成协办.
     */
    void resolveTask(String taskId);

    /**
     * 根据activityId找到任务定义.
     */
    ProcessTaskDefinition findTaskDefinition(String processDefinitionId,
            String taskDefintionKey, String businessKey);

    /**
     * 获得流程发起人.
     */
    String findInitiator(String processInstanceId);

    /**
     * 获得某个节点的历史负责人.
     */
    String findAssigneeByActivityId(String processInstanceId, String activityId);

    /**
     * 解析表达式.
     */
    Object executeExpression(String taskId, String expressionText);

    /**
     * 获得开始事件id.
     */
    String findInitialActivityId(String processDefinitionId);

    /**
     * 获得第一个UserTask的节点id.
     */
    String findFirstUserTaskActivityId(String processDefinitionId,
            String initiator);

    /**
     * 触发execution继续执行.
     */
    void signalExecution(String executionId);
}
