package com.mossle.bpm.cmd;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskConstants;
import com.mossle.api.humantask.HumanTaskDTO;

import com.mossle.bpm.graph.ActivitiHistoryGraphBuilder;
import com.mossle.bpm.graph.Edge;
import com.mossle.bpm.graph.Graph;
import com.mossle.bpm.graph.Node;
import com.mossle.bpm.support.HumanTaskBuilder;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.HistoricActivityInstanceQueryImpl;
import org.activiti.engine.impl.HistoricTaskInstanceQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.cmd.GetStartFormCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 退回任务.
 */
public class RollbackTaskCmd implements Command<Object> {
    /** logger. */
    private static Logger logger = LoggerFactory
            .getLogger(RollbackTaskCmd.class);

    /** task id. */
    private String taskId;

    /** activity id. */
    private String activityId;

    /** user id. */
    private String userId;

    /** use last assignee. */
    private boolean useLastAssignee = false;

    /** 需要处理的多实例节点. */
    private Set<String> multiInstanceExecutionIds = new HashSet<String>();

    /**
     * 指定taskId和跳转到的activityId，自动使用最后的assignee.
     */
    public RollbackTaskCmd(String taskId, String activityId) {
        this.taskId = taskId;
        this.activityId = activityId;
        this.useLastAssignee = true;
    }

    /**
     * 指定taskId和跳转到的activityId, userId.
     */
    public RollbackTaskCmd(String taskId, String activityId, String userId) {
        this.taskId = taskId;
        this.activityId = activityId;
        this.userId = userId;
    }

    /**
     * 退回流程.
     * 
     * @return 0-退回成功 1-流程结束 2-下一结点已经通过,不能退回
     */
    public Integer execute(CommandContext commandContext) {
        // 获得任务
        TaskEntity taskEntity = this.findTask(commandContext);

        // 找到想要回退到的节点
        ActivityImpl targetActivity = this.findTargetActivity(commandContext,
                taskEntity);
        logger.info("rollback to {}", this.activityId);
        logger.info("{}", targetActivity.getProperties());

        String type = (String) targetActivity.getProperty("type");

        if ("userTask".equals(type)) {
            logger.info("rollback to userTask");
            this.rollbackUserTask(commandContext);
        } else if ("startEvent".equals(type)) {
            logger.info("rollback to startEvent");
            this.rollbackStartEvent(commandContext);
        } else {
            throw new IllegalStateException("cannot rollback " + type);
        }

        return 0;
    }

    /**
     * 回退到userTask.
     */
    public Integer rollbackUserTask(CommandContext commandContext) {
        // 获得任务
        TaskEntity taskEntity = this.findTask(commandContext);

        // 找到想要回退到的节点
        ActivityImpl targetActivity = this.findTargetActivity(commandContext,
                taskEntity);

        // 找到想要回退对应的节点历史
        HistoricActivityInstanceEntity historicActivityInstanceEntity = this
                .findTargetHistoricActivity(commandContext, taskEntity,
                        targetActivity);

        // 找到想要回退对应的任务历史
        HistoricTaskInstanceEntity historicTaskInstanceEntity = this
                .findTargetHistoricTask(commandContext, taskEntity,
                        targetActivity);

        logger.info("historic activity instance is : {}",
                historicActivityInstanceEntity.getId());

        Graph graph = new ActivitiHistoryGraphBuilder(
                historicTaskInstanceEntity.getProcessInstanceId()).build();

        Node node = graph.findById(historicActivityInstanceEntity.getId());

        if (!checkCouldRollback(node)) {
            logger.info("cannot rollback {}", taskId);

            return 2;
        }

        if (this.isSameBranch(historicTaskInstanceEntity)) {
            // 如果退回的目标节点的executionId与当前task的executionId一样，说明是同一个分支
            // 只删除当前分支的task
            TaskEntity targetTaskEntity = Context.getCommandContext()
                    .getTaskEntityManager().findTaskById(this.taskId);
            this.deleteActiveTask(targetTaskEntity);
        } else {
            // 否则认为是从分支跳回主干
            // 删除所有活动中的task
            this.deleteActiveTasks(historicTaskInstanceEntity
                    .getProcessInstanceId());

            // 获得期望退回的节点后面的所有节点历史
            List<String> historyNodeIds = new ArrayList<String>();
            collectNodes(node, historyNodeIds);
            this.deleteHistoryActivities(historyNodeIds);
        }

        // 处理多实例
        this.processMultiInstance();

        // 恢复期望退回的任务和历史
        this.processHistoryTask(commandContext, taskEntity,
                historicTaskInstanceEntity, historicActivityInstanceEntity);

        logger.info("activiti is rollback {}",
                historicTaskInstanceEntity.getName());

        return 0;
    }

    /**
     * 回退到startEvent.
     */
    public Integer rollbackStartEvent(CommandContext commandContext) {
        // 获得任务
        TaskEntity taskEntity = this.findTask(commandContext);

        // 找到想要回退到的节点
        ActivityImpl targetActivity = this.findTargetActivity(commandContext,
                taskEntity);

        if (taskEntity.getExecutionId().equals(
                taskEntity.getProcessInstanceId())) {
            // 如果退回的目标节点的executionId与当前task的executionId一样，说明是同一个分支
            // 只删除当前分支的task
            TaskEntity targetTaskEntity = Context.getCommandContext()
                    .getTaskEntityManager().findTaskById(this.taskId);
            this.deleteActiveTask(targetTaskEntity);
        } else {
            // 否则认为是从分支跳回主干
            // 删除所有活动中的task
            this.deleteActiveTasks(taskEntity.getProcessInstanceId());
        }

        // 把流程指向任务对应的节点
        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager()
                .findExecutionById(taskEntity.getExecutionId());
        executionEntity.setActivity(targetActivity);

        // 创建HistoricActivityInstance
        Context.getCommandContext().getHistoryManager()
                .recordActivityStart(executionEntity);

        String processDefinitionId = taskEntity.getProcessDefinitionId();
        GetStartFormCmd getStartFormCmd = new GetStartFormCmd(
                processDefinitionId);
        StartFormData startFormData = getStartFormCmd.execute(commandContext);

        try {
            logger.info("{}", targetActivity.getProperties());

            // humanTask
            HumanTaskConnector humanTaskConnector = ApplicationContextHelper
                    .getBean(HumanTaskConnector.class);
            HumanTaskDTO humanTaskDto = humanTaskConnector.createHumanTask();
            humanTaskDto.setName((String) targetActivity.getProperty("name"));
            humanTaskDto.setDescription((String) targetActivity
                    .getProperty("description"));
            humanTaskDto.setCode(targetActivity.getId());
            humanTaskDto.setAssignee(this.userId);
            humanTaskDto.setOwner(null);
            humanTaskDto.setDelegateStatus("none");
            humanTaskDto.setPriority(50);
            humanTaskDto.setCreateTime(new Date());
            humanTaskDto.setDuration(null);
            humanTaskDto.setSuspendStatus("none");
            humanTaskDto.setCategory("startEvent");
            humanTaskDto.setForm(startFormData.getFormKey());
            humanTaskDto.setTaskId(null);
            humanTaskDto.setExecutionId(taskEntity.getExecutionId());
            humanTaskDto
                    .setProcessInstanceId(taskEntity.getProcessInstanceId());
            humanTaskDto.setProcessDefinitionId(taskEntity
                    .getProcessDefinitionId());
            humanTaskDto.setTenantId(taskEntity.getTenantId());
            humanTaskDto.setStatus("active");
            humanTaskDto = humanTaskConnector.saveHumanTask(humanTaskDto);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        // 处理多实例
        this.processMultiInstance();

        return 0;
    }

    /**
     * 获得当前任务.
     */
    public TaskEntity findTask(CommandContext commandContext) {
        TaskEntity taskEntity = commandContext.getTaskEntityManager()
                .findTaskById(taskId);

        return taskEntity;
    }

    /**
     * 查找回退的目的节点.
     */
    public ActivityImpl findTargetActivity(CommandContext commandContext,
            TaskEntity taskEntity) {
        if (activityId == null) {
            String historyTaskId = this.findNearestUserTask(commandContext);
            HistoricTaskInstanceEntity historicTaskInstanceEntity = commandContext
                    .getHistoricTaskInstanceEntityManager()
                    .findHistoricTaskInstanceById(historyTaskId);
            this.activityId = historicTaskInstanceEntity.getTaskDefinitionKey();
        }

        String processDefinitionId = taskEntity.getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId).execute(commandContext);

        return processDefinitionEntity.findActivity(activityId);
    }

    /**
     * 找到想要回退对应的节点历史.
     */
    public HistoricActivityInstanceEntity findTargetHistoricActivity(
            CommandContext commandContext, TaskEntity taskEntity,
            ActivityImpl activityImpl) {
        HistoricActivityInstanceQueryImpl historicActivityInstanceQueryImpl = new HistoricActivityInstanceQueryImpl();
        historicActivityInstanceQueryImpl.activityId(activityImpl.getId());
        historicActivityInstanceQueryImpl.processInstanceId(taskEntity
                .getProcessInstanceId());
        historicActivityInstanceQueryImpl
                .orderByHistoricActivityInstanceEndTime().desc();

        HistoricActivityInstanceEntity historicActivityInstanceEntity = (HistoricActivityInstanceEntity) commandContext
                .getHistoricActivityInstanceEntityManager()
                .findHistoricActivityInstancesByQueryCriteria(
                        historicActivityInstanceQueryImpl, new Page(0, 1))
                .get(0);

        return historicActivityInstanceEntity;
    }

    /**
     * 找到想要回退对应的任务历史.
     */
    public HistoricTaskInstanceEntity findTargetHistoricTask(
            CommandContext commandContext, TaskEntity taskEntity,
            ActivityImpl activityImpl) {
        HistoricTaskInstanceQueryImpl historicTaskInstanceQueryImpl = new HistoricTaskInstanceQueryImpl();
        historicTaskInstanceQueryImpl.taskDefinitionKey(activityImpl.getId());
        historicTaskInstanceQueryImpl.processInstanceId(taskEntity
                .getProcessInstanceId());
        historicTaskInstanceQueryImpl.setFirstResult(0);
        historicTaskInstanceQueryImpl.setMaxResults(1);
        historicTaskInstanceQueryImpl.orderByTaskCreateTime().desc();

        HistoricTaskInstanceEntity historicTaskInstanceEntity = (HistoricTaskInstanceEntity) commandContext
                .getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstancesByQueryCriteria(
                        historicTaskInstanceQueryImpl).get(0);

        return historicTaskInstanceEntity;
    }

    /**
     * 判断想要回退的目标节点和当前节点是否在一个分支上.
     */
    public boolean isSameBranch(
            HistoricTaskInstanceEntity historicTaskInstanceEntity) {
        TaskEntity taskEntity = Context.getCommandContext()
                .getTaskEntityManager().findTaskById(taskId);

        return taskEntity.getExecutionId().equals(
                historicTaskInstanceEntity.getExecutionId());
    }

    /**
     * 查找离当前节点最近的上一个userTask.
     */
    public String findNearestUserTask(CommandContext commandContext) {
        TaskEntity taskEntity = commandContext.getTaskEntityManager()
                .findTaskById(taskId);

        if (taskEntity == null) {
            logger.debug("cannot find task : {}", taskId);

            return null;
        }

        Graph graph = new ActivitiHistoryGraphBuilder(
                taskEntity.getProcessInstanceId()).build();
        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        String historicActivityInstanceId = jdbcTemplate.queryForObject(
                "SELECT ID_ FROM ACT_HI_ACTINST WHERE TASK_ID_=?",
                String.class, taskId);
        Node node = graph.findById(historicActivityInstanceId);

        String previousHistoricActivityInstanceId = this.findIncomingNode(
                graph, node);

        if (previousHistoricActivityInstanceId == null) {
            logger.debug(
                    "cannot find previous historic activity instance : {}",
                    taskEntity);

            return null;
        }

        return jdbcTemplate.queryForObject(
                "SELECT TASK_ID_ FROM ACT_HI_ACTINST WHERE ID_=?",
                String.class, previousHistoricActivityInstanceId);
    }

    /**
     * 查找进入的连线.
     */
    public String findIncomingNode(Graph graph, Node node) {
        for (Edge edge : graph.getEdges()) {
            Node src = edge.getSrc();
            Node dest = edge.getDest();
            String srcType = src.getType();

            if (!dest.getId().equals(node.getId())) {
                continue;
            }

            if ("userTask".equals(srcType)) {
                boolean isSkip = isSkipActivity(src.getId());

                if (isSkip) {
                    return this.findIncomingNode(graph, src);
                } else {
                    return src.getId();
                }
            } else if (srcType.endsWith("Gateway")) {
                return this.findIncomingNode(graph, src);
            } else {
                logger.info("cannot rollback, previous node is not userTask : "
                        + src.getId() + " " + srcType + "(" + src.getName()
                        + ")");

                return null;
            }
        }

        logger.info("cannot rollback, this : " + node.getId() + " "
                + node.getType() + "(" + node.getName() + ")");

        return null;
    }

    /**
     * 查询历史节点.
     */
    public HistoricActivityInstanceEntity getHistoricActivityInstanceEntity(
            String historyTaskId) {
        logger.info("historyTaskId : {}", historyTaskId);

        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        String historicActivityInstanceId = jdbcTemplate.queryForObject(
                "SELECT ID_ FROM ACT_HI_ACTINST WHERE TASK_ID_=?",
                String.class, historyTaskId);
        logger.info("historicActivityInstanceId : {}",
                historicActivityInstanceId);

        HistoricActivityInstanceQueryImpl historicActivityInstanceQueryImpl = new HistoricActivityInstanceQueryImpl();
        historicActivityInstanceQueryImpl
                .activityInstanceId(historicActivityInstanceId);

        HistoricActivityInstanceEntity historicActivityInstanceEntity = (HistoricActivityInstanceEntity) Context
                .getCommandContext()
                .getHistoricActivityInstanceEntityManager()
                .findHistoricActivityInstancesByQueryCriteria(
                        historicActivityInstanceQueryImpl, new Page(0, 1))
                .get(0);

        return historicActivityInstanceEntity;
    }

    /**
     * 判断是否可回退.
     */
    public boolean checkCouldRollback(Node node) {
        // TODO: 如果是catchEvent，也应该可以退回，到时候再说
        for (Edge edge : node.getOutgoingEdges()) {
            Node dest = edge.getDest();
            String type = dest.getType();

            if ("userTask".equals(type)) {
                if (!dest.isActive()) {
                    boolean isSkip = isSkipActivity(dest.getId());

                    if (isSkip) {
                        return checkCouldRollback(dest);
                    } else {
                        // logger.info("cannot rollback, " + type + "("
                        // + dest.getName() + ") is complete.");
                        // return false;
                        return true;
                    }
                }
            } else if (type.endsWith("Gateway")) {
                return checkCouldRollback(dest);
            } else {
                logger.info("cannot rollback, " + type + "(" + dest.getName()
                        + ") is complete.");

                return false;
            }
        }

        return true;
    }

    /**
     * 删除活动状态任务.
     */
    public void deleteActiveTasks(String processInstanceId) {
        List<TaskEntity> taskEntities = Context.getCommandContext()
                .getTaskEntityManager()
                .findTasksByProcessInstanceId(processInstanceId);

        for (TaskEntity taskEntity : taskEntities) {
            this.deleteActiveTask(taskEntity);
        }
    }

    /**
     * 遍历节点.
     */
    public void collectNodes(Node node, List<String> historyNodeIds) {
        logger.info("node : {}, {}, {}", node.getId(), node.getType(),
                node.getName());

        for (Edge edge : node.getOutgoingEdges()) {
            logger.info("edge : {}", edge.getName());

            Node dest = edge.getDest();
            historyNodeIds.add(dest.getId());
            collectNodes(dest, historyNodeIds);
        }
    }

    public void deleteHistoryActivities(List<String> historyNodeIds) {
        /*
         * JdbcTemplate jdbcTemplate = ApplicationContextHelper .getBean(JdbcTemplate.class);
         * logger.info("historyNodeIds : {}", historyNodeIds);
         * 
         * for (String id : historyNodeIds) { jdbcTemplate.update("delete from ACT_HI_ACTINST where id_=?", id); }
         */
    }

    /**
     * 根据任务历史，创建待办任务.
     */
    public void processHistoryTask(CommandContext commandContext,
            TaskEntity taskEntity,
            HistoricTaskInstanceEntity historicTaskInstanceEntity,
            HistoricActivityInstanceEntity historicActivityInstanceEntity) {
        if (this.userId == null) {
            if (this.useLastAssignee) {
                this.userId = historicTaskInstanceEntity.getAssignee();
            } else {
                String processDefinitionId = taskEntity
                        .getProcessDefinitionId();
                ProcessDefinitionEntity processDefinitionEntity = new GetDeploymentProcessDefinitionCmd(
                        processDefinitionId).execute(commandContext);
                TaskDefinition taskDefinition = processDefinitionEntity
                        .getTaskDefinitions().get(
                                historicTaskInstanceEntity
                                        .getTaskDefinitionKey());

                this.userId = (String) taskDefinition.getAssigneeExpression()
                        .getValue(taskEntity);
            }
        }

        /*
         * historicTaskInstanceEntity.setEndTime(null); historicTaskInstanceEntity.setDurationInMillis(null);
         * historicActivityInstanceEntity.setEndTime(null); historicActivityInstanceEntity.setDurationInMillis(null);
         */

        // 创建新任务
        TaskEntity task = TaskEntity.create(new Date());
        task.setProcessDefinitionId(historicTaskInstanceEntity
                .getProcessDefinitionId());
        // task.setId(historicTaskInstanceEntity.getId());
        task.setAssigneeWithoutCascade(userId);
        task.setParentTaskIdWithoutCascade(historicTaskInstanceEntity
                .getParentTaskId());
        task.setNameWithoutCascade(historicTaskInstanceEntity.getName());
        task.setTaskDefinitionKey(historicTaskInstanceEntity
                .getTaskDefinitionKey());
        task.setExecutionId(historicTaskInstanceEntity.getExecutionId());
        task.setPriority(historicTaskInstanceEntity.getPriority());
        task.setProcessInstanceId(historicTaskInstanceEntity
                .getProcessInstanceId());
        task.setExecutionId(historicTaskInstanceEntity.getExecutionId());
        task.setDescriptionWithoutCascade(historicTaskInstanceEntity
                .getDescription());
        task.setTenantId(historicTaskInstanceEntity.getTenantId());

        Context.getCommandContext().getTaskEntityManager().insert(task);

        // 把流程指向任务对应的节点
        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager()
                .findExecutionById(historicTaskInstanceEntity.getExecutionId());
        executionEntity
                .setActivity(getActivity(historicActivityInstanceEntity));

        // 创建HistoricActivityInstance
        Context.getCommandContext().getHistoryManager()
                .recordActivityStart(executionEntity);

        // 创建HistoricTaskInstance
        Context.getCommandContext().getHistoryManager()
                .recordTaskCreated(task, executionEntity);
        Context.getCommandContext().getHistoryManager().recordTaskId(task);
        // 更新ACT_HI_ACTIVITY里的assignee字段
        Context.getCommandContext().getHistoryManager()
                .recordTaskAssignment(task);

        try {
            // humanTask
            this.createHumanTask(task, historicTaskInstanceEntity);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获得历史节点对应的节点信息.
     */
    public ActivityImpl getActivity(
            HistoricActivityInstanceEntity historicActivityInstanceEntity) {
        ProcessDefinitionEntity processDefinitionEntity = new GetDeploymentProcessDefinitionCmd(
                historicActivityInstanceEntity.getProcessDefinitionId())
                .execute(Context.getCommandContext());

        return processDefinitionEntity
                .findActivity(historicActivityInstanceEntity.getActivityId());
    }

    /**
     * 删除未完成任务.
     */
    public void deleteActiveTask(TaskEntity taskEntity) {
        ProcessDefinitionEntity processDefinitionEntity = new GetDeploymentProcessDefinitionCmd(
                taskEntity.getProcessDefinitionId()).execute(Context
                .getCommandContext());

        ActivityImpl activityImpl = processDefinitionEntity
                .findActivity(taskEntity.getTaskDefinitionKey());

        if (this.isMultiInstance(activityImpl)) {
            logger.info("{} is multiInstance", taskEntity.getId());
            this.multiInstanceExecutionIds.add(taskEntity.getExecution()
                    .getParent().getId());
            logger.info("append : {}", taskEntity.getExecution().getParent()
                    .getId());
        }

        // TaskEntity taskEntity = Context.getCommandContext()
        // .getTaskEntityManager().findTaskById(this.taskId);
        Context.getCommandContext().getTaskEntityManager()
                .deleteTask(taskEntity, "回退", false);

        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        List<Map<String, Object>> list = jdbcTemplate
                .queryForList(
                        "SELECT * FROM ACT_HI_ACTINST WHERE TASK_ID_=? AND END_TIME_ IS NULL",
                        taskId);
        Date now = new Date();

        for (Map<String, Object> map : list) {
            Date startTime = (Date) map.get("START_TIME_");
            long duration = now.getTime() - startTime.getTime();
            jdbcTemplate
                    .update("UPDATE ACT_HI_ACTINST SET END_TIME_=?,DURATION_=? WHERE ID_=?",
                            now, duration, map.get("ID_"));
        }

        // 处理humanTask
        HumanTaskConnector humanTaskConnector = ApplicationContextHelper
                .getBean(HumanTaskConnector.class);
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTaskByTaskId(this.taskId);
        humanTaskDto.setCompleteTime(new Date());
        humanTaskDto.setStatus("rollback");
        humanTaskConnector.saveHumanTask(humanTaskDto);
    }

    /**
     * 判断跳过节点.
     */
    public boolean isSkipActivity(String historyActivityId) {
        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        String historyTaskId = jdbcTemplate.queryForObject(
                "SELECT TASK_ID_ FROM ACT_HI_ACTINST WHERE ID_=?",
                String.class, historyActivityId);

        HistoricTaskInstanceEntity historicTaskInstanceEntity = Context
                .getCommandContext().getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(historyTaskId);
        String deleteReason = historicTaskInstanceEntity.getDeleteReason();

        return "跳过".equals(deleteReason);
    }

    /**
     * 创建humanTask.
     */
    public HumanTaskDTO createHumanTask(DelegateTask delegateTask,
            HistoricTaskInstanceEntity historicTaskInstanceEntity)
            throws Exception {
        HumanTaskConnector humanTaskConnector = ApplicationContextHelper
                .getBean(HumanTaskConnector.class);
        HumanTaskDTO humanTaskDto = new HumanTaskBuilder().setDelegateTask(
                delegateTask).build();

        if ("发起流程".equals(historicTaskInstanceEntity.getDeleteReason())) {
            humanTaskDto.setCatalog(HumanTaskConstants.CATALOG_START);
        }

        HistoricProcessInstance historicProcessInstance = Context
                .getCommandContext()
                .getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(
                        delegateTask.getProcessInstanceId());
        humanTaskDto
                .setProcessStarter(historicProcessInstance.getStartUserId());
        humanTaskDto = humanTaskConnector.saveHumanTask(humanTaskDto);

        return humanTaskDto;
    }

    /**
     * 判断是否会签.
     */
    public boolean isMultiInstance(PvmActivity pvmActivity) {
        return pvmActivity.getProperty("multiInstance") != null;
    }

    /**
     * 处理多实例.
     */
    public void processMultiInstance() {
        logger.info("multiInstanceExecutionIds : {}", multiInstanceExecutionIds);

        for (String executionId : multiInstanceExecutionIds) {
            ExecutionEntity parent = Context.getCommandContext()
                    .getExecutionEntityManager().findExecutionById(executionId);
            List<ExecutionEntity> children = Context.getCommandContext()
                    .getExecutionEntityManager()
                    .findChildExecutionsByParentExecutionId(parent.getId());

            for (ExecutionEntity executionEntity : children) {
                executionEntity.remove();
            }

            parent.remove();
        }
    }
}
