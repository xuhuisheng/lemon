package com.mossle.bpm.cmd;

import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务和活动相关命令的命令
 * 
 * @author zouping
 * 
 */
public abstract class ActivityCmd {
    private static Logger logger = LoggerFactory.getLogger(ActivityCmd.class);
    protected ExecutionEntity execution;
    protected ProcessDefinitionEntity processDefinition;
    protected ExecutionEntity processInstance;

    /**
     * 根据executionId获取Execution
     * 
     * @param executionId
     * @return
     */
    public ExecutionEntity getExecution(String executionId) {
        ExecutionEntity execution = Context.getCommandContext()
                .getExecutionEntityManager().findExecutionById(executionId);

        if (execution == null) {
            throw new ActivitiException("未找到Execution<" + executionId + ">");
        }

        return execution;
    }

    /**
     * 返回流程定义
     * 
     * @return
     */
    public ProcessDefinitionEntity getProcessDefinition() {
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) execution
                .getProcessDefinition();

        if (processDefinition == null) {
            throw new ActivitiException("未找到流程实例Execution<" + execution.getId()
                    + ">");
        }

        return processDefinition;
    }

    /**
     * 获取活动节点ActivityImpl
     * 
     * @param activityName
     * @return
     */
    public ActivityImpl getActivity(String activityId) {
        ActivityImpl activity = getProcessDefinition().findActivity(activityId);

        if (activity == null) {
            throw new ActivitiException("can not find the " + activityId
                    + " in processDefinition");
        }

        return activity;
    }

    /**
     * 根据节点Id获取HistoricTaskInstanceEntity
     * 
     * @param activityId
     *            节点名称
     * @return
     */
    public HistoricTaskInstanceEntity getHistoricTaskInstance(String activityId) {
        List<HistoricTaskInstance> list = Context.getCommandContext()
                .getDbSqlSession().createHistoricTaskInstanceQuery()
                .executionId(execution.getId()).taskDefinitionKey(activityId)
                .list();

        if (list.size() == 0) {
            throw new ActivitiException("获取历史任务实例出错，原因：找不到executionId=" + ""
                    + execution.getId() + ",activityName=" + activityId);
        }

        if (list.size() > 1) {
            // throw new ActivitiException("获取历史任务实例出错，原因：存在多个历史任务实例。executionId="
            // + "" + execution.getId() + ",activityName=" + activityId);
            list = Context.getCommandContext().getDbSqlSession()
                    .createHistoricTaskInstanceQuery()
                    .executionId(execution.getId())
                    .taskDefinitionKey(activityId)
                    .orderByHistoricActivityInstanceId().desc().list();
        }

        return (HistoricTaskInstanceEntity) list.get(0);
    }

    /**
     * 根据节点Id获取HistoricActivityInstanceEntity
     * 
     * @param activityId
     *            节点名称
     * @return
     */
    public HistoricActivityInstanceEntity getHistoricActivityInstance(
            String activityId) {
        List<HistoricActivityInstance> list = Context.getCommandContext()
                .getDbSqlSession().createHistoricActivityInstanceQuery()
                .executionId(execution.getId()).activityId(activityId)
                .orderByHistoricActivityInstanceStartTime().desc().list();

        if (list.size() == 0) {
            throw new ActivitiException("获取历史活动实例出错，原因：找不到executionId=" + ""
                    + execution.getId() + ",activityName=" + activityId);
        }

        if (list.size() > 1) {
            logger.info(
                    "获取历史活动实例出错，原因：存在多个历史活动实例。executionId={},activityName={}",
                    execution.getId(), activityId);
        }

        return (HistoricActivityInstanceEntity) list.get(0);
    }

    /**
     * 获取历史活动节点 only if the task and previous task are in the same execution can be rollBacked in this version.
     * 
     * @return
     */
    public List<HistoricActivityInstance> getProcessInstanceHistoryActivities() {
        return Context.getCommandContext().getDbSqlSession()
                .createHistoricActivityInstanceQuery()
                .executionId(execution.getId()).list();
    }
}
