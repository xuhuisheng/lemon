package com.mossle.bpm.cmd;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.context.Context;
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
 * 收回流程 目前?虑一种情况下的流程会?单箭头无分支
 * 
 * @author zou_ping
 * 
 */
public class WithdrawTaskCmd extends TaskCmd implements Command<Integer> {
    private static Logger logger = LoggerFactory
            .getLogger(WithdrawTaskCmd.class);
    private String historyTaskId;
    private HistoricActivityInstanceEntity historicActivityInstance;
    private HistoricTaskInstanceEntity historicTaskInstance;
    private HistoricActivityInstanceEntity nextHistoricActivityInstance;
    private HistoricTaskInstanceEntity nextHistoricTaskInstance;
    private TaskEntity nextTask;

    public WithdrawTaskCmd(String historyTaskId) {
        this.historyTaskId = historyTaskId;
    }

    /**
     * 撤销流程
     * 
     * @return 0-撤销成功 1-流程结束 2-下一结点已经通过,不能撤销
     */
    public Integer execute(CommandContext commandContext) {
        int code = initAndCheck();
        logger.info("activiti is withdraw {}", historicTaskInstance.getName());

        if (code == 0) {
            processHistoryTask();
            processNextTask();
            logger.info("withdraw task({}->{}) successfully",
                    historicTaskInstance.getName(),
                    nextHistoricTaskInstance.getName());
        }

        return code;
    }

    public void initHistoricTaskInstanceEntity() {
        historicTaskInstance = Context.getCommandContext()
                .getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(historyTaskId);
    }

    public void initHistoricActivityInstanceEntity() {
        historicActivityInstance = getHistoricActivityInstance(historicTaskInstance
                .getTaskDefinitionKey());
    }

    /**
     * initial task context,execution,historyActivityInstanceImpl
     */
    public int initAndCheck() {
        initHistoricTaskInstanceEntity();

        if (historicTaskInstance == null) {
            throw new ActivitiException("task<" + historyTaskId + ">,无法找到此任");
        }

        execution = Context.getCommandContext().getExecutionEntityManager()
                .findExecutionById(historicTaskInstance.getExecutionId());

        if (execution == null) {
            logger.info("task<" + historyTaskId + ">,当前流程已经结束,不能撤销");

            return 1;
        }

        initHistoricActivityInstanceEntity();

        processDefinition = getProcessDefinition();

        if (processDefinition == null) {
            throw new ActivitiException("task<" + historyTaskId
                    + ">,无法找到此任务所在的流程定义");
        }

        if (!isNextTask()) {
            logger.info("task<" + historyTaskId + ">任务已经到下??无法收回");

            return 2;
        }

        nextTask = getCurrentTask();
        // nextHistoricActivityInstance = Context.getCommandContext()
        // .getHistoricActivityInstanceEntityManager()
        // .findHistoricActivityInstance(execution.getActivityId(),
        // execution.getProcessInstanceId());
        nextHistoricActivityInstance = (HistoricActivityInstanceEntity) Context
                .getCommandContext().getDbSqlSession()
                .createHistoricActivityInstanceQuery()
                .executionId(execution.getId())
                .activityId(execution.getActivityId())
                .orderByHistoricActivityInstanceStartTime().desc().list()
                .get(0);

        nextHistoricTaskInstance = Context.getCommandContext()
                .getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(nextTask.getId());

        return 0;
    }

    /**
     * 获取该节点的下一节点
     * 
     * @param activityId
     * @return
     */
    public HistoricActivityInstance findNextHistoryActivityInstance(
            String activityId) {
        List<PvmTransition> transitions = getActivity(activityId)
                .getOutgoingTransitions();
        List<HistoricActivityInstance> historicActivityInstances = getProcessInstanceHistoryActivities();

        for (PvmTransition transition : transitions) {
            TransitionImpl transitionImpl = (TransitionImpl) transition;
            ActivityImpl sourceActivity = transitionImpl.getDestination();

            for (HistoricActivityInstance hai : historicActivityInstances) {
                if (sourceActivity.getId().equals(hai.getActivityId())) {
                    return hai;
                }
            }
        }

        return null;
    }

    /**
     * 判断该节点的下一节点为流程当前节?
     * 
     * @return
     */
    public boolean isNextTask() {
        HistoricActivityInstance nextHistoricActivityInstance = findNextHistoryActivityInstance(historicActivityInstance
                .getActivityId());
        String currentProcessActivityId = execution.getActivityId();

        if (currentProcessActivityId.equals(nextHistoricActivityInstance
                .getActivityId())) {
            return true;
        }

        return false;
    }

    /**
     * 处理当前历史任务流程<br/>
     * 1.将historytask.endTime 置为null<br/>
     * 2.新建当前Task<br/>
     * 3.将Execution设置为Task<br/>
     */
    public void processHistoryTask() {
        historicTaskInstance.setEndTime(null);
        historicTaskInstance.setDurationInMillis(null);
        historicActivityInstance.setEndTime(null);
        historicActivityInstance.setDurationInMillis(null);

        TaskEntity task = TaskEntity.create();
        task.setProcessDefinitionId(historicTaskInstance
                .getProcessDefinitionId());
        // 注意,task和historicTask的id是一样的
        task.setId(historicTaskInstance.getId());
        task.setAssigneeWithoutCascade(historicTaskInstance.getAssignee());
        task.setParentTaskIdWithoutCascade(historicTaskInstance
                .getParentTaskId());
        task.setNameWithoutCascade(historicTaskInstance.getName());
        task.setTaskDefinitionKey(historicTaskInstance.getTaskDefinitionKey());
        task.setExecutionId(historicTaskInstance.getExecutionId());
        task.setPriority(historicTaskInstance.getPriority());
        task.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
        task.setDescriptionWithoutCascade(historicTaskInstance.getDescription());

        Context.getCommandContext().getTaskEntityManager().insert(task);
        execution.setActivity(getActivity(historicActivityInstance
                .getActivityId()));
    }

    /**
     * 处理下一节点Task 1.删除HistoryTask 2.删除Task
     * 
     * @return
     */
    public void processNextTask() {
        /**
         * 删除历史活动节点,因为activiti5.6没有映射deleteHistoricActivityInstance这一statement ?历史节点考虑用纯Sql语句删除
         */
        Connection conn = Context.getCommandContext().getDbSqlSession()
                .getSqlSession().getConnection();
        Statement state = null;

        try {
            String sql = "delete from ACT_HI_ACTINST where ID_="
                    + nextHistoricActivityInstance.getId();
            state = conn.createStatement();
            state.execute(sql);
        } catch (SQLException e) {
            throw new ActivitiException("sql语句执行失败", e);
        } finally {
            try {
                if (state != null) {
                    state.close();
                    state = null;
                }
            } catch (SQLException ex) {
                logger.debug(ex.getMessage(), ex);
            }

            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex) {
                logger.debug(ex.getMessage(), ex);
            }
        }

        // Context
        // .getCommandContext()
        // .getHistoricActivityInstanceManager()
        // .delete(nextHistoricActivityInstance);

        // 删除历史任务节点
        Context.getCommandContext()
                .getHistoricTaskInstanceEntityManager()
                .deleteHistoricTaskInstanceById(
                        nextHistoricTaskInstance.getId());

        // 删除任务节点
        Context.getCommandContext().getTaskEntityManager().delete(nextTask);
    }
}
