package com.mossle.bpm.cmd;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.history.handler.ActivityInstanceStartHandler;
import org.activiti.engine.impl.history.handler.UserTaskAssignmentHandler;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流程打回 流程命令,目前只考虑一种情况下的流程会单箭头无分支
 * 
 * @author zou_ping
 * 
 */
public class RollbackTaskCmd extends TaskCmd implements Command<Integer> {
    private static Logger logger = LoggerFactory
            .getLogger(RollbackTaskCmd.class);
    private TaskEntity task;
    private HistoricActivityInstanceEntity historicActivityInstance;
    private HistoricTaskInstanceEntity preHistoricTaskInstance;
    private HistoricActivityInstanceEntity preHistoricActivityInstance;

    public RollbackTaskCmd(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 命令执行
     * 
     * @return 0-成功 1-为初始节点无法回退
     */
    public Integer execute(CommandContext commandContext) {
        int code = initAndCheck();
        logger.info("activiti is rollbacking task {}", task.getName());

        if (code == 0) {
            processTask();

            TaskEntity preTask = processPreTask();
            logger.info("rollback task({}->{}) successfully", task.getName(),
                    preTask.getName());
        }

        return code;
    }

    /**
     * 初始化并检测 initial task context,execution,historyActivityInstanceImpl
     */
    public int initAndCheck() {
        task = getTask(taskId);

        if (task == null) {
            throw new ActivitiException("task<" + taskId + ">,无法找到此任");
        }

        execution = task.getExecution();

        if (execution == null) {
            throw new ActivitiException("task<" + taskId + ">,无法找到此任务所在的流程执行实例");
        }

        processDefinition = getProcessDefinition();

        if (processDefinition == null) {
            throw new ActivitiException("task<" + taskId + ">,无法找到此任务所在的流程定义");
        }

        if (isFirstTask()) {
            logger.info("task<" + task.getId() + ">为初始节点无法回滚");

            return 1;
        }

        historicActivityInstance = this.getHistoricActivityInstance(task
                .getTaskDefinitionKey());
        preHistoricActivityInstance = (HistoricActivityInstanceEntity) findPreviousHistoryActivityInstance(task
                .getTaskDefinitionKey());

        if (preHistoricActivityInstance == null) {
            throw new ActivitiException("task<" + taskId + ">,无法找到此任务的上一个任务节点");
        }

        preHistoricTaskInstance = (HistoricTaskInstanceEntity) findPreviousHistoryTaskInstance(task
                .getTaskDefinitionKey());

        if (preHistoricTaskInstance == null) {
            throw new ActivitiException("task<" + task.getId()
                    + ">无法找到上一级历史活动节点");
        }

        return 0;
    }

    /**
     * 处理当前任务 1.删除当前任务 2.删除历史任务 3.删除历史活动节点
     */
    public void processTask() {
        // 删除当前任务
        Context.getCommandContext().getTaskEntityManager()
                .deleteTask(task, TaskEntity.DELETE_REASON_DELETED, false);

        TaskEntity task = Context.getCommandContext().getTaskEntityManager()
                .findTaskById(taskId);

        // 结束历史任务
        HistoricTaskInstanceEntity historicTaskInstance = Context
                .getCommandContext().getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(taskId);
        historicTaskInstance.markEnded("退回");

        // 记录节点历史
        HistoricActivityInstanceEntity historicActivityInstance = Context
                .getCommandContext()
                .getHistoricActivityInstanceEntityManager()
                .findHistoricActivityInstance(
                        task.getExecution().getCurrentActivityId(),
                        task.getProcessInstanceId());
        historicActivityInstance.markEnded("退回");

        /**
         * 结束历史活动节点,因为activiti5.6没有映射updateHistoricActivityInstance这一statement 历史节点考虑用纯Sql语句更新
         */
        try {
            String sql = "update ACT_HI_ACTINST set END_TIME_=?,DURATION_=? where ID_=?";

            // + historicActivityInstance.getId();
            PreparedStatement state = Context.getCommandContext()
                    .getDbSqlSession().getSqlSession().getConnection()
                    .prepareStatement(sql);
            Date now = new Date();
            state.setTimestamp(1, new java.sql.Timestamp(now.getTime()));
            state.setLong(2, now.getTime()
                    - historicActivityInstance.getStartTime().getTime());
            state.setLong(3, Long.parseLong(historicActivityInstance.getId()));
            state.executeUpdate();
            state.close();
        } catch (SQLException e) {
            throw new ActivitiException("sql语句执行失败", e);
        }

        // Context
        // .getCommandContext()
        // .getHistoricActivityInstanceManager()
        // .deleteHistoricActivityInstance(historicActivityInstance.getId());
    }

    /**
     * 处理前一个任务节?确保RU_TASK和HI_TASKINST的ID_是一样的,否则重新完成时两个任务不会关? 1.增加前任? 2.将前历史任务结束时间置为?duration置空,delete_resons_置空
     * 3.将前历史活动节点结束时间置为? 4.将Execution指针指向前任务节点
     * 
     * @return Task
     */
    public TaskEntity processPreTask() {
        // 增加前任务节?
        TaskEntity preTask = TaskEntity.create();

        // 确保当前任务和历史任务的ID是一样的，相当于还原
        preTask.setId(UUID.randomUUID().toString()); // 防止验证历史出现空指针

        // DbSqlSession dbSqlSession = Context.getCommandContext()
        // .getDbSqlSession();
        // dbSqlSession.insert(preTask);

        // 获取任务节点定义
        ActivityImpl activity = getActivity(preHistoricActivityInstance
                .getActivityId());

        if (activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
            UserTaskActivityBehavior behavior = (UserTaskActivityBehavior) activity
                    .getActivityBehavior();
            preTask.setTaskDefinition(behavior.getTaskDefinition());
        }

        preTask.setExecution(execution);
        preTask.setAssignee(preHistoricTaskInstance.getAssignee());
        preTask.setName(preHistoricTaskInstance.getName());
        preTask.setOwner(preHistoricTaskInstance.getOwner());
        preTask.setPriority(preHistoricTaskInstance.getPriority());
        // 加描述或?回评?
        preTask.setDescription(preHistoricTaskInstance.getDescription()
                + "<rollback>");
        preTask.setExecutionId(execution.getId());
        preTask.setProcessInstanceId(execution.getProcessInstanceId());
        preTask.setProcessDefinitionId(execution.getProcessDefinitionId());

        // 将前历史任务结束时间置为?
        /*
         * preHistoricTaskInstance.setEndTime(null); preHistoricTaskInstance.setDurationInMillis(null);
         * preHistoricTaskInstance.setDeleteReason(null);
         */

        // 将前历史活动节点结束时间置为?
        /*
         * preHistoricActivityInstance.setEndTime(null); preHistoricActivityInstance.setDurationInMillis(null);
         * preHistoricActivityInstance.setDeleteReason(null);
         */

        // 更新Execution
        execution.setActivity(activity);

        // 创建新的HistoryActivityInstance
        new ActivityInstanceStartHandler().notify(execution);
        new UserTaskAssignmentHandler().notify(preTask);

        // 创建新的HistoryTaskInstance
        preTask.insert(execution);

        return preTask;
    }

    /**
     * getPreviousHistoryTask by taskName
     * 
     * @param activityId
     * @return HistoryTask
     */
    public HistoricTaskInstance findPreviousHistoryTaskInstance(
            String activityId) {
        HistoricActivityInstance hai = findPreviousHistoryActivityInstance(activityId);

        if (hai != null) {
            if (hai.getActivityType().equals("userTask")) {
                this.preHistoricActivityInstance = (HistoricActivityInstanceEntity) hai;

                return getHistoricTaskInstance(hai.getActivityId());
            } else { // iterate

                return findPreviousHistoryTaskInstance(hai.getActivityId());
            }
        }

        return null;
    }

    /**
     * getPreviousHistoryActivityInstance by activityName,not only for the task
     * 
     * @param activityId
     * @return HistoryActivityInstance
     */
    public HistoricActivityInstance findPreviousHistoryActivityInstance(
            String activityId) {
        List<PvmTransition> transitions = getActivity(activityId)
                .getIncomingTransitions();
        List<HistoricActivityInstance> historicActivityInstances = getProcessInstanceHistoryActivities();

        for (PvmTransition transition : transitions) {
            TransitionImpl transitionImpl = (TransitionImpl) transition;
            ActivityImpl sourceActivity = transitionImpl.getSource();

            for (HistoricActivityInstance hai : historicActivityInstances) {
                if (sourceActivity.getId().equals(hai.getActivityId())) {
                    return hai;
                }
            }
        }

        return null;
    }
}
