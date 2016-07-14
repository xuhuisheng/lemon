package com.mossle.bpm.cmd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * 撤销任务.
 */
public class WithdrawTaskCmd implements Command<Integer> {
    private static Logger logger = LoggerFactory
            .getLogger(WithdrawTaskCmd.class);
    private String historyTaskId;

    /**
     * 这个historyTaskId是已经完成的一个任务的id.
     */
    public WithdrawTaskCmd(String historyTaskId) {
        this.historyTaskId = historyTaskId;
    }

    /**
     * 撤销流程.
     * 
     * @return 0-撤销成功 1-流程结束 2-下一结点已经通过,不能撤销
     */
    public Integer execute(CommandContext commandContext) {
        // 获得历史任务
        HistoricTaskInstanceEntity historicTaskInstanceEntity = Context
                .getCommandContext().getHistoricTaskInstanceEntityManager()
                .findHistoricTaskInstanceById(historyTaskId);

        // 获得历史节点
        HistoricActivityInstanceEntity historicActivityInstanceEntity = getHistoricActivityInstanceEntity(historyTaskId);

        Graph graph = new ActivitiHistoryGraphBuilder(
                historicTaskInstanceEntity.getProcessInstanceId()).build();

        Node node = graph.findById(historicActivityInstanceEntity.getId());

        if (!checkCouldWithdraw(node)) {
            logger.info("cannot withdraw {}", historyTaskId);

            return 2;
        }

        // 删除所有活动中的task
        this.deleteActiveTasks(historicTaskInstanceEntity
                .getProcessInstanceId());

        // 获得期望撤销的节点后面的所有节点历史
        List<String> historyNodeIds = new ArrayList<String>();
        this.collectNodes(node, historyNodeIds);
        this.deleteHistoryActivities(historyNodeIds);
        // 恢复期望撤销的任务和历史
        this.processHistoryTask(historicTaskInstanceEntity,
                historicActivityInstanceEntity);

        logger.info("activiti is withdraw {}",
                historicTaskInstanceEntity.getName());

        return 0;
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

    public boolean checkCouldWithdraw(Node node) {
        // TODO: 如果是catchEvent，也应该可以撤销，到时候再说
        for (Edge edge : node.getOutgoingEdges()) {
            Node dest = edge.getDest();
            String type = dest.getType();

            if ("userTask".equals(type)) {
                if (!dest.isActive()) {
                    boolean isSkip = isSkipActivity(dest.getId());

                    if (isSkip) {
                        return checkCouldWithdraw(dest);
                    } else {
                        logger.info("cannot withdraw, " + type + "("
                                + dest.getName() + ") is complete.");

                        return false;
                    }
                }
            } else if (type.endsWith("Gateway")) {
                return checkCouldWithdraw(dest);
            } else {
                logger.info("cannot withdraw, " + type + "(" + dest.getName()
                        + ") is complete.");

                return false;
            }
        }

        return true;
    }

    /**
     * 删除未完成任务.
     */
    public void deleteActiveTasks(String processInstanceId) {
        // humantask
        HumanTaskConnector humanTaskConnector = ApplicationContextHelper
                .getBean(HumanTaskConnector.class);
        humanTaskConnector
                .removeHumanTaskByProcessInstanceId(processInstanceId);

        Context.getCommandContext().getTaskEntityManager()
                .deleteTasksByProcessInstanceId(processInstanceId, null, true);
    }

    public void collectNodes(Node node, List<String> historyNodeIds) {
        logger.info("node : {}, {}, {}", node.getId(), node.getType(),
                node.getName());

        for (Edge edge : node.getOutgoingEdges()) {
            logger.info("edge : {}", edge.getName());

            Node dest = edge.getDest();
            historyNodeIds.add(dest.getId());
            this.collectNodes(dest, historyNodeIds);
        }
    }

    /**
     * 删除历史节点.
     */
    public void deleteHistoryActivities(List<String> historyNodeIds) {
        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);
        logger.info("historyNodeIds : {}", historyNodeIds);

        for (String id : historyNodeIds) {
            String taskId = jdbcTemplate.queryForObject(
                    "select task_id_ from ACT_HI_ACTINST where id_=?",
                    String.class, id);

            if (taskId != null) {
                Context.getCommandContext()
                        .getHistoricTaskInstanceEntityManager()
                        .deleteHistoricTaskInstanceById(taskId);
            }

            jdbcTemplate.update("delete from ACT_HI_ACTINST where id_=?", id);
        }
    }

    public void processHistoryTask(
            HistoricTaskInstanceEntity historicTaskInstanceEntity,
            HistoricActivityInstanceEntity historicActivityInstanceEntity) {
        historicTaskInstanceEntity.setEndTime(null);
        historicTaskInstanceEntity.setDurationInMillis(null);
        historicActivityInstanceEntity.setEndTime(null);
        historicActivityInstanceEntity.setDurationInMillis(null);

        TaskEntity task = TaskEntity.create(new Date());
        task.setProcessDefinitionId(historicTaskInstanceEntity
                .getProcessDefinitionId());
        task.setId(historicTaskInstanceEntity.getId());
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
        task.setDescriptionWithoutCascade(historicTaskInstanceEntity
                .getDescription());
        task.setTenantId(historicTaskInstanceEntity.getTenantId());

        Context.getCommandContext().getTaskEntityManager().insert(task);

        try {
            HumanTaskConnector humanTaskConnector = ApplicationContextHelper
                    .getBean(HumanTaskConnector.class);
            // humantask
            humanTaskConnector
                    .removeHumanTaskByTaskId(historicTaskInstanceEntity.getId());
            this.createHumanTask(task, historicTaskInstanceEntity);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        ExecutionEntity executionEntity = Context.getCommandContext()
                .getExecutionEntityManager()
                .findExecutionById(historicTaskInstanceEntity.getExecutionId());
        executionEntity
                .setActivity(getActivity(historicActivityInstanceEntity));
    }

    public ActivityImpl getActivity(
            HistoricActivityInstanceEntity historicActivityInstanceEntity) {
        ProcessDefinitionEntity processDefinitionEntity = new GetDeploymentProcessDefinitionCmd(
                historicActivityInstanceEntity.getProcessDefinitionId())
                .execute(Context.getCommandContext());

        return processDefinitionEntity
                .findActivity(historicActivityInstanceEntity.getActivityId());
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
