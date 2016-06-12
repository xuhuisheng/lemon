package com.mossle.spi.humantask;

import java.util.List;

public interface TaskDefinitionConnector {
    /**
     * 获取分配策略.
     */
    String findTaskAssignStrategy(String taskDefinitionKey,
            String processDefinitionId);

    /**
     * 获取会签配置.
     */
    CounterSignDTO findCounterSign(String taskDefinitionKey,
            String processDefinitionId);

    /**
     * 获取表单配置.
     */
    FormDTO findForm(String taskDefinitionKey, String processDefinitionId);

    /**
     * 获取操作配置.
     */
    List<String> findOperations(String taskDefinitionKey,
            String processDefinitionId);

    /**
     * 获取参与者.
     */
    List<TaskUserDTO> findTaskUsers(String taskDefinitionKey,
            String processDefinitionId);

    /**
     * 获取截止日期.
     */
    List<DeadlineDTO> findDeadlines(String taskDefinitionKey,
            String processDefinitionId);

    /**
     * 获取流程实例对应的参与者配置.
     */
    String findTaskConfUser(String taskDefinitionKey, String businessKey);

    /**
     * 获取提醒配置.
     */
    List<TaskNotificationDTO> findTaskNotifications(String taskDefinitionKey,
            String processDefinitionId, String eventName);

    /**
     * 创建新的任务定义.
     */
    void create(TaskDefinitionDTO taskDefinition);

    /**
     * 保存分配策略.
     */
    void saveAssignStrategy(String taskDefinitionKey,
            String processDefinitoinId, String assigneeStrategy);

    /**
     * 保存会签配置.
     */
    void saveCounterSign(String taskDefinitionKey, String processDefinitionId,
            CounterSignDTO counterSign);

    /**
     * 保存表单配置.
     */
    void saveForm(String taskDefinitionKey, String processDefinitionId,
            FormDTO form);

    /**
     * 添加操作.
     */
    void addOperation(String taskDefinitionKey, String processDefinitionId,
            String operation);

    /**
     * 删除操作.
     */
    void removeOperation(String taskDefinitionKey, String processDefinitionId,
            String operation);

    /**
     * 添加参与者.
     */
    void addTaskUser(String taskDefinitionKey, String processDefinitionId,
            TaskUserDTO taskUser);

    /**
     * 删除参与者.
     */
    void removeTaskUser(String taskDefinitionKey, String processDefinitionId,
            TaskUserDTO taskUser);

    /**
     * 更新参与者.
     */
    void updateTaskUser(String taskDefinitionKey, String processDefinitionId,
            TaskUserDTO taskUser, String status);

    /**
     * 新增提醒.
     */
    void addTaskNotification(String taskDefinitionKey,
            String processDefinitionId, TaskNotificationDTO taskNotification);

    /**
     * 删除提醒.
     */
    void removeTaskNotification(String taskDefinitionKey,
            String processDefinitionId, TaskNotificationDTO taskNotification);

    /**
     * 新增截止.
     */
    void addDeadline(String taskDefinitionKey, String processDefinitionId,
            DeadlineDTO deadline);

    /**
     * 删除截止.
     */
    void removeDeadline(String taskDefinitionKey, String processDefinitionId,
            DeadlineDTO deadline);
}
