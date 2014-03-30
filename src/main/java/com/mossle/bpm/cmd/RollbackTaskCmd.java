package com.mossle.bpm.cmd;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.bpm.graph.ActivitiHistoryGraphBuilder;
import com.mossle.bpm.graph.Edge;
import com.mossle.bpm.graph.Graph;
import com.mossle.bpm.graph.Node;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricActivityInstance;
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
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 退回任务.
 */
public class RollbackTaskCmd implements Command<Integer> {
    private static Logger logger = LoggerFactory
            .getLogger(RollbackTaskCmd.class);
    private String taskId;

    /**
     * 这个taskId是运行阶段task的id.
     */
    public RollbackTaskCmd(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 退回流程.
     * 
     * @return 0-退回成功 1-流程结束 2-下一结点已经通过,不能退回
     */
    public Integer execute(CommandContext commandContext) {
        // 尝试查找最近的上游userTask
        String historyTaskId = this.findNearestUserTask();

        if (historyTaskId == null) {
            logger.info("cannot rollback {}", taskId);

            return 2;
        }

        // 先找到历史任务
        HistoricTaskInstanceEntity historicTaskInstanceEntity = Context
                .getCommandContext().getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(historyTaskId);

        // 再反向查找历史任务对应的历史节点
        HistoricActivityInstanceEntity historicActivityInstanceEntity = getHistoricActivityInstanceEntity(historyTaskId);

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
            this.deleteActiveTask();
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

        // 恢复期望退回的任务和历史
        this.processHistoryTask(historicTaskInstanceEntity,
                historicActivityInstanceEntity);

        logger.info("activiti is rollback {}",
                historicTaskInstanceEntity.getName());

        return 0;
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
        for (Edge edge : node.getEdges()) {
            Node dest = edge.getDest();
            String type = dest.getType();

            if ("userTask".equals(type)) {
                if (!dest.isActive()) {
                    boolean isSkip = isSkipActivity(dest.getId());

                    if (isSkip) {
                        return checkCouldRollback(dest);
                    } else {
                        logger.info("cannot rollback, " + type + "("
                                + dest.getName() + ") is complete.");

                        return false;
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

        for (Edge edge : node.getEdges()) {
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
        task.setAssigneeWithoutCascade(historicTaskInstanceEntity.getAssignee());
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
                .getTaskEntityManager().findTaskById(taskId);
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
    }

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
}
