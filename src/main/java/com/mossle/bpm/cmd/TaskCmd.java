package com.mossle.bpm.cmd;

import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.TaskQueryImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.task.Task;

public abstract class TaskCmd extends ActivityCmd {
    /** 要处理的任务ID **/
    protected String taskId;

    /**
     * 初始化并判断任务是否可以撤销
     */
    public abstract int initAndCheck();

    /**
     * 根据任务Id获取任务实例
     * 
     * @param taskId
     * @return
     */
    public TaskEntity getTask(String taskId) {
        TaskEntity task = Context.getCommandContext().getTaskEntityManager()
                .findTaskById(taskId);

        return task;
    }

    /**
     * 判断是否为第一个初始节点
     * 
     * @return
     */
    public boolean isFirstTask() {
        ActivityImpl rootActivity = execution.getProcessDefinition()
                .getInitial();
        List<PvmTransition> transitions = rootActivity.getOutgoingTransitions();

        for (PvmTransition transition : transitions) {
            TransitionImpl transitionImpl = (TransitionImpl) transition;
            ActivityImpl destinationActivity = transitionImpl.getDestination();
            String firstTaskActivityname = destinationActivity.getId();
            String currentActivityName = execution.getActivity().getId();

            return currentActivityName.equals(firstTaskActivityname);
        }

        return false;
    }

    /**
     * 获取当前任务
     * 
     * @return
     */
    public TaskEntity getCurrentTask() {
        ActivityBehavior activityBehavior = execution.getActivity()
                .getActivityBehavior();

        if (!(activityBehavior instanceof UserTaskActivityBehavior)) {
            throw new ActivitiException("当前节点不是任务类型<" + execution.getId()
                    + ">," + activityBehavior.getClass());
        }

        TaskQueryImpl taskQueryImpl = new TaskQueryImpl();
        taskQueryImpl.taskDefinitionKey(execution.getActivityId()).executionId(
                execution.getId());
        taskQueryImpl.setFirstResult(0);
        taskQueryImpl.setMaxResults(1);

        List<Task> tasks = Context.getCommandContext().getTaskEntityManager()
                .findTasksByQueryCriteria(taskQueryImpl);

        if (tasks.size() == 0) {
            throw new ActivitiException("未找到当前任务" + execution.getId() + ">");
        }

        return (TaskEntity) tasks.get(0);
    }
}
