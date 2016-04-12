package com.mossle.bpm.cmd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskConstants;
import com.mossle.api.humantask.HumanTaskDTO;

import com.mossle.bpm.graph.ActivitiHistoryGraphBuilder;
import com.mossle.bpm.graph.Edge;
import com.mossle.bpm.graph.Graph;
import com.mossle.bpm.graph.Node;
import com.mossle.bpm.support.HumanTaskBuilder;
import com.mossle.bpm.support.JumpInfo;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.HistoricActivityInstanceQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 退回.
 */
public class RollbackCmd implements Command<Boolean> {
    private static Logger logger = LoggerFactory.getLogger(RollbackCmd.class);
    public static final String ACTIVITY_PREVIOUS = "ACTIVITY_PREVIOUS";
    public static final String ASSIGNEE_LAST = "ASSIGNEE_LAST";
    public static final String ASSIGNEE_AUTO = "ASSIGNEE_AUTO";
    private String taskId;
    private String activityId;
    private String userId;
    private JumpInfo jumpInfo = new JumpInfo();

    /**
     * 这个taskId是运行阶段task的id.
     */
    public RollbackCmd(String taskId, String activityId, String userId) {
        this.taskId = taskId;
        this.activityId = activityId;
        this.userId = userId;
    }

    public void initSource() {
        // source task
        this.jumpInfo.setSourceTaskId(this.taskId);

        TaskEntity sourceTask = Context.getCommandContext()
                .getTaskEntityManager().findTaskById(this.taskId);
        this.jumpInfo.setSourceTask(sourceTask);

        ProcessDefinitionEntity processDefinitionEntity = Context
                .getProcessEngineConfiguration()
                .getDeploymentManager()
                .findDeployedProcessDefinitionById(
                        sourceTask.getProcessDefinitionId());
        // source activity
        this.jumpInfo.setSourceActivityId(sourceTask.getTaskDefinitionKey());
        this.jumpInfo.setSourceActivity(processDefinitionEntity
                .findActivity(this.jumpInfo.getSourceActivityId()));

        HistoricTaskInstanceEntity sourceHistoryTask = Context
                .getCommandContext().getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(this.jumpInfo.getSourceTaskId());
    }

    /**
     * 退回流程.
     * 
     * @return true success, false failure
     */
    public Boolean execute(CommandContext commandContext) {
        // 初始化当前的起点信息
        this.initSource();

        // 找到回退的目标节点
        String targetActivityId = this.findTargetActivityId();

        if (targetActivityId == null) {
            logger.info("cannot find targetActivity for : {}", this.taskId);

            return Boolean.FALSE;
        }

        /*
         * // 尝试查找最近的上游userTask String historyTaskId = this.findNearestUserTask();
         * logger.info("nearest history user task is : {}", historyTaskId);
         * 
         * if (historyTaskId == null) { logger.info("cannot rollback {}", taskId);
         * 
         * return "activity"; }
         */

        // 校验这个节点是否可以回退
        boolean isValid = this.validateTargetActivity(targetActivityId);

        if (!isValid) {
            logger.info("cannot rollback for : {} to {}", this.taskId,
                    targetActivityId);

            return Boolean.FALSE;
        }

        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        String historyTaskId = jdbcTemplate
                .queryForObject(
                        "select id_ from ACT_HI_TASKINST where act_id_=? order by END_TIME_ desc",
                        String.class, targetActivityId);
        HistoricTaskInstanceEntity historicTaskInstanceEntity = Context
                .getCommandContext().getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(historyTaskId);
        HistoricActivityInstanceEntity historicActivityInstanceEntity = getHistoricActivityInstanceEntity(historyTaskId);

        // 开始回退
        if (this.isSameBranch(historicTaskInstanceEntity)) {
            // 如果退回的目标节点的executionId与当前task的executionId一样，说明是同一个分支
            // 只删除当前分支的task
            this.deleteActiveTask();
        } else {
            // 否则认为是从分支跳回主干
            // 删除所有活动中的task
            this.deleteActiveTasks(historicTaskInstanceEntity
                    .getProcessInstanceId());

            // 获得期望退回的节点后面的所有节点历史
            List<String> historyNodeIds = new ArrayList<String>();
            Graph graph = new ActivitiHistoryGraphBuilder(
                    historicTaskInstanceEntity.getProcessInstanceId()).build();

            Node node = graph.findById(historicActivityInstanceEntity.getId());
            this.collectNodes(node, historyNodeIds);
            this.deleteHistoryActivities(historyNodeIds);
        }

        // 恢复期望退回的任务和历史
        this.processHistoryTask(historicTaskInstanceEntity,
                historicActivityInstanceEntity);

        logger.info("activiti is rollback {}",
                historicTaskInstanceEntity.getName());

        return Boolean.TRUE;
    }

    /**
     * 找到目的地activityId.
     */
    public String findTargetActivityId() {
        if (ACTIVITY_PREVIOUS.equals(this.activityId)) {
            String taskId = this.findNearestUserTask();
            TaskEntity taskEntity = Context.getCommandContext()
                    .getTaskEntityManager().findTaskById(taskId);

            return taskEntity.getTaskDefinitionKey();
        } else {
            return this.activityId;
        }
    }

    /**
     * 校验目标节点是否可以回退.
     */
    public boolean validateTargetActivity(String targetActivityId) {
        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        String historyTaskId = jdbcTemplate
                .queryForObject(
                        "select id_ from ACT_HI_TASKINST where act_id_=? order by END_TIME_ desc",
                        String.class, targetActivityId);

        // 先找到历史任务
        HistoricTaskInstanceEntity historicTaskInstanceEntity = Context
                .getCommandContext().getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(historyTaskId);

        // 再反向查找历史任务对应的历史节点
        HistoricActivityInstanceEntity historicActivityInstanceEntity = getHistoricActivityInstanceEntity(historyTaskId);

        logger.info("historic activity instance is : {}",
                historicActivityInstanceEntity.getId());

        Graph graph = new ActivitiHistoryGraphBuilder(
                historicTaskInstanceEntity.getProcessInstanceId()).build();

        Node node = graph.findById(historicActivityInstanceEntity.getId());

        if (!this.checkCouldRollback(node)) {
            logger.info("cannot rollback {}", taskId);

            return false;
        }

        return true;
    }

    public boolean isSameBranch(
            HistoricTaskInstanceEntity historicTaskInstanceEntity) {
        TaskEntity taskEntity = Context.getCommandContext()
                .getTaskEntityManager().findTaskById(taskId);

        return taskEntity.getExecutionId().equals(
                historicTaskInstanceEntity.getExecutionId());
    }

    public String findNearestUserTask() {
        TaskEntity taskEntity = Context.getCommandContext()
                .getTaskEntityManager().findTaskById(taskId);

        if (taskEntity == null) {
            logger.debug("cannot find task : {}", taskId);

            return null;
        }

        Graph graph = new ActivitiHistoryGraphBuilder(
                taskEntity.getProcessInstanceId()).build();
        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        String historicActivityInstanceId = jdbcTemplate.queryForObject(
                "select id_ from ACT_HI_ACTINST where task_id_=?",
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
                "select task_id_ from ACT_HI_ACTINST where id_=?",
                String.class, previousHistoricActivityInstanceId);
    }

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

    public HistoricActivityInstanceEntity getHistoricActivityInstanceEntity(
            String historyTaskId) {
        logger.info("historyTaskId : {}", historyTaskId);

        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        String historicActivityInstanceId = jdbcTemplate.queryForObject(
                "select id_ from ACT_HI_ACTINST where task_id_=?",
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

    public void deleteActiveTasks(String processInstanceId) {
        Context.getCommandContext().getTaskEntityManager()
                .deleteTasksByProcessInstanceId(processInstanceId, "退回", false);

        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        List<Map<String, Object>> list = jdbcTemplate
                .queryForList(
                        "select * from ACT_HI_ACTINST where proc_inst_id_=? and end_time_ is null",
                        processInstanceId);
        Date now = new Date();

        for (Map<String, Object> map : list) {
            Date startTime = (Date) map.get("start_time_");
            long duration = now.getTime() - startTime.getTime();
            jdbcTemplate
                    .update("update ACT_HI_ACTINST set end_time_=?,duration_=? where id_=?",
                            now, duration, map.get("id_"));
        }
    }

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
    public void processHistoryTask(
            HistoricTaskInstanceEntity historicTaskInstanceEntity,
            HistoricActivityInstanceEntity historicActivityInstanceEntity) {
        /*
         * historicTaskInstanceEntity.setEndTime(null); historicTaskInstanceEntity.setDurationInMillis(null);
         * historicActivityInstanceEntity.setEndTime(null); historicActivityInstanceEntity.setDurationInMillis(null);
         */

        // 创建新任务
        TaskEntity task = TaskEntity.create(new Date());
        task.setProcessDefinitionId(historicTaskInstanceEntity
                .getProcessDefinitionId());
        // task.setId(historicTaskInstanceEntity.getId());
        // task.setAssigneeWithoutCascade(historicTaskInstanceEntity.getAssignee());
        task.setAssigneeWithoutCascade(this.userId);
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
    public void deleteActiveTask() {
        TaskEntity taskEntity = Context.getCommandContext()
                .getTaskEntityManager().findTaskById(this.taskId);
        Context.getCommandContext().getTaskEntityManager()
                .deleteTask(taskEntity, "回退", false);

        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        List<Map<String, Object>> list = jdbcTemplate
                .queryForList(
                        "select * from ACT_HI_ACTINST where task_id_=? and end_time_ is null",
                        taskId);
        Date now = new Date();

        for (Map<String, Object> map : list) {
            Date startTime = (Date) map.get("start_time_");
            long duration = now.getTime() - startTime.getTime();
            jdbcTemplate
                    .update("update ACT_HI_ACTINST set end_time_=?,duration_=? where id_=?",
                            now, duration, map.get("id_"));
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
                "select task_id_ from ACT_HI_ACTINST where id_=?",
                String.class, historyActivityId);

        HistoricTaskInstanceEntity historicTaskInstanceEntity = Context
                .getCommandContext().getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(historyTaskId);
        String deleteReason = historicTaskInstanceEntity.getDeleteReason();

        return "跳过".equals(deleteReason);
    }

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
}
