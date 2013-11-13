package com.mossle.bpm.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CounterSignCmd implements Command<Object> {
    private static Logger log = LoggerFactory.getLogger(CounterSignCmd.class);
    private String operateType;
    private String activityId;
    private String assignee;
    private String processInstanceId;
    private String collectionVariableName;
    private String collectionElementVariableName;
    private CommandContext commandContext;
    private String taskId;

    public CounterSignCmd(String operateType, String assignee, String taskId) {
        this.operateType = operateType;
        this.assignee = assignee;
        this.taskId = taskId;
    }

    /**
     * @param operateType
     *            操作类型 add or remove
     * @param activityId
     *            节点ID
     * @param assignee
     *            人员代码
     * @param processInstanceId
     *            流程实例ID
     * @param collectionVariableName
     *            collection 设置的变量名
     * @param collectionElementVariableName
     *            collection 的每个元素变量名
     */
    public CounterSignCmd(final String operateType, final String activityId,
            final String assignee, final String processInstanceId,
            final String collectionVariableName,
            final String collectionElementVariableName) {
        this.operateType = operateType;
        this.activityId = activityId;
        this.assignee = assignee;
        this.processInstanceId = processInstanceId;
        this.collectionVariableName = collectionVariableName;
        this.collectionElementVariableName = collectionElementVariableName;
    }

    public Object execute(CommandContext commandContext) {
        this.commandContext = commandContext;

        if (this.taskId != null) {
            TaskEntity taskEntity = commandContext.getTaskEntityManager()
                    .findTaskById(taskId);
            activityId = taskEntity.getExecution().getActivityId();
            processInstanceId = taskEntity.getProcessInstanceId();
            this.collectionVariableName = "countersignUsers";
            this.collectionElementVariableName = "countersignUser";
        }

        if (operateType.equalsIgnoreCase("add")) {
            addInstance();
        } else if (operateType.equalsIgnoreCase("remove")) {
            removeInstance();
        }

        return null;
    }

    /**
     * <li>加签
     */
    public void addInstance() {
        if (isParallel()) {
            addParallelInstance();
        } else {
            addSequentialInstance();
        }
    }

    /**
     * <li>减签
     */
    public void removeInstance() {
        if (isParallel()) {
            removeParallelInstance();
        } else {
            removeSequentialInstance();
        }
    }

    /**
     * <li>添加一条并行实例
     */
    private void addParallelInstance() {
        ExecutionEntity parentExecutionEntity = commandContext
                .getExecutionEntityManager()
                .findExecutionById(processInstanceId).findExecution(activityId);
        ActivityImpl activity = getActivity();
        ExecutionEntity execution = parentExecutionEntity.createExecution();
        execution.setActive(true);
        execution.setConcurrent(true);
        execution.setScope(false);

        if (getActivity().getProperty("type").equals("subProcess")) {
            ExecutionEntity extraScopedExecution = execution.createExecution();
            extraScopedExecution.setActive(true);
            extraScopedExecution.setConcurrent(false);
            extraScopedExecution.setScope(true);
            execution = extraScopedExecution;
        }

        setLoopVariable(parentExecutionEntity, "nrOfInstances",
                (Integer) parentExecutionEntity
                        .getVariableLocal("nrOfInstances") + 1);
        setLoopVariable(parentExecutionEntity, "nrOfActiveInstances",
                (Integer) parentExecutionEntity
                        .getVariableLocal("nrOfActiveInstances") + 1);
        setLoopVariable(execution, "loopCounter", parentExecutionEntity
                .getExecutions().size() + 1);
        setLoopVariable(execution, collectionElementVariableName, assignee);
        execution.executeActivity(activity);
    }

    /**
     * <li>给串行实例集合中添加一个审批人
     */
    private void addSequentialInstance() {
        ExecutionEntity execution = getActivieExecutions().get(0);

        if (getActivity().getProperty("type").equals("subProcess")) {
            if (!execution.isActive()
                    && execution.isEnded()
                    && ((execution.getExecutions() == null) || (execution
                            .getExecutions().size() == 0))) {
                execution.setActive(true);
            }
        }

        Collection<String> col = (Collection<String>) execution
                .getVariable(collectionVariableName);
        col.add(assignee);
        execution.setVariable(collectionVariableName, col);
        setLoopVariable(execution, "nrOfInstances",
                (Integer) execution.getVariableLocal("nrOfInstances") + 1);
    }

    /**
     * <li>移除一条并行实例
     */
    private void removeParallelInstance() {
        List<ExecutionEntity> executions = getActivieExecutions();

        for (ExecutionEntity executionEntity : executions) {
            String executionVariableAssignee = (String) executionEntity
                    .getVariableLocal(collectionElementVariableName);

            if ((executionVariableAssignee != null)
                    && executionVariableAssignee.equals(assignee)) {
                executionEntity.remove();

                ExecutionEntity parentConcurrentExecution = executionEntity
                        .getParent();

                if (getActivity().getProperty("type").equals("subProcess")) {
                    parentConcurrentExecution = parentConcurrentExecution
                            .getParent();
                }

                setLoopVariable(parentConcurrentExecution, "nrOfInstances",
                        (Integer) parentConcurrentExecution
                                .getVariableLocal("nrOfInstances") - 1);
                setLoopVariable(parentConcurrentExecution,
                        "nrOfActiveInstances",
                        (Integer) parentConcurrentExecution
                                .getVariableLocal("nrOfActiveInstances") - 1);

                break;
            }
        }
    }

    /**
     * <li>冲串行列表中移除未完成的用户(当前执行的用户无法移除)
     */
    private void removeSequentialInstance() {
        ExecutionEntity executionEntity = getActivieExecutions().get(0);
        Collection<String> col = (Collection<String>) executionEntity
                .getVariable(collectionVariableName);
        log.info("移除前审批列表 : {}", col.toString());
        col.remove(assignee);
        executionEntity.setVariable(collectionVariableName, col);
        setLoopVariable(executionEntity, "nrOfInstances",
                (Integer) executionEntity.getVariableLocal("nrOfInstances") - 1);

        // 如果串行要删除的人是当前active执行,
        if (executionEntity.getVariableLocal(collectionElementVariableName)
                .equals(assignee)) {
            throw new ActivitiException("当前正在执行的实例,无法移除!");
        }

        log.info("移除后审批列表 : {}", col.toString());
    }

    /**
     * <li>获取活动的执行 , 子流程的活动执行是其孩子执行(并行多实例情况下) <li>串行情况下获取的结果数量为1
     */
    protected List<ExecutionEntity> getActivieExecutions() {
        List<ExecutionEntity> activeExecutions = new ArrayList<ExecutionEntity>();
        ActivityImpl activity = getActivity();
        List<ExecutionEntity> executions = getChildExecutionByProcessInstanceId();

        for (ExecutionEntity execution : executions) {
            if (execution.isActive()
                    && (execution.getActivityId().equals(activityId) || activity
                            .contains(execution.getActivity()))) {
                activeExecutions.add(execution);
            }
        }

        return activeExecutions;
    }

    /**
     * <li>获取流程实例根的所有子执行
     */
    protected List<ExecutionEntity> getChildExecutionByProcessInstanceId() {
        return commandContext.getExecutionEntityManager()
                .findChildExecutionsByProcessInstanceId(processInstanceId);
    }

    /**
     * <li>返回当前节点对象
     */
    protected ActivityImpl getActivity() {
        return this.getProcessDefinition().findActivity(activityId);
    }

    /**
     * <li>判断节点多实例类型是否是并发
     */
    protected boolean isParallel() {
        return getActivity().getProperty("multiInstance").equals("parallel");
    }

    /**
     * <li>返回流程定义对象
     */
    protected ProcessDefinitionImpl getProcessDefinition() {
        return this.getProcessInstanceEntity().getProcessDefinition();
    }

    /**
     * <li>返回流程实例的根执行对象
     */
    protected ExecutionEntity getProcessInstanceEntity() {
        return commandContext.getExecutionEntityManager().findExecutionById(
                processInstanceId);
    }

    /**
     * <li>添加本地变量
     */
    protected void setLoopVariable(ActivityExecution execution,
            String variableName, Object value) {
        execution.setVariableLocal(variableName, value);
    }
}
