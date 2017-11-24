package com.mossle.bpm.listener;

import java.util.Collections;
import java.util.List;

import com.mossle.api.org.OrgConnector;
import com.mossle.api.user.UserConnector;

import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;
import com.mossle.bpm.persistence.domain.BpmConfRule;
import com.mossle.bpm.persistence.manager.BpmConfRuleManager;
import com.mossle.bpm.support.DefaultTaskListener;
import com.mossle.bpm.support.MapVariableScope;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class SkipTaskListener extends DefaultTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(SkipTaskListener.class);

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String processDefinitionId = delegateTask.getProcessDefinitionId();
        String processInstanceId = delegateTask.getProcessInstanceId();
        HistoricProcessInstanceEntity historicProcessInstanceEntity = Context
                .getCommandContext().getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(processInstanceId);

        List<BpmConfRule> bpmConfRules = ApplicationContextHelper
                .getBean(BpmConfRuleManager.class)
                .find("from BpmConfRule where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, taskDefinitionKey);
        logger.debug("delegateTask.getId : {}", delegateTask.getId());
        logger.debug("taskDefinitionKey : {}", taskDefinitionKey);
        logger.debug("processDefinitionId : {}", processDefinitionId);
        logger.debug("processInstanceId : {}", processInstanceId);
        logger.debug("bpmConfRules : {}", bpmConfRules);

        for (BpmConfRule bpmConfRule : bpmConfRules) {
            String value = bpmConfRule.getValue();

            if ("职位".equals(value) || "高级职位自动跳过".equals(value)) {
                this.processPosition(delegateTask, value);
            } else if ("相邻相同人员自动跳过".equals(value)) {
                this.processNeighbor(delegateTask, value);
            } else {
                this.processExpression(delegateTask, value);
            }
        }
    }

    public void processPosition(DelegateTask delegateTask, String value) {
        String processInstanceId = delegateTask.getProcessInstanceId();
        HistoricProcessInstanceEntity historicProcessInstanceEntity = Context
                .getCommandContext().getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(processInstanceId);
        String initiator = historicProcessInstanceEntity.getStartUserId();
        OrgConnector orgConnector = (OrgConnector) ApplicationContextHelper
                .getBean(OrgConnector.class);

        // 获得发起人的职位
        int initiatorLevel = orgConnector.getJobLevelByUserId(initiator);

        // 获得审批人的职位
        int assigneeLevel = orgConnector.getJobLevelByUserId(delegateTask
                .getAssignee());

        // 比较
        if (initiatorLevel >= assigneeLevel) {
            logger.info("skip task : {}", delegateTask.getId());
            logger.info("initiatorLevel : {}, assigneeLevel : {}",
                    initiatorLevel, assigneeLevel);
            new CompleteTaskWithCommentCmd(delegateTask.getId(),
                    Collections.<String, Object> emptyMap(), "高级职位自动跳过")
                    .execute(Context.getCommandContext());
        }
    }

    public void processNeighbor(DelegateTask delegateTask, String value) {
        String processDefinitionId = delegateTask.getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = Context
                .getProcessEngineConfiguration().getProcessDefinitionCache()
                .get(processDefinitionId);
        ActivityImpl activityImpl = processDefinitionEntity
                .findActivity(delegateTask.getTaskDefinitionKey());
        PvmTransition pvmTransition = activityImpl.getIncomingTransitions()
                .iterator().next();
        PvmActivity pvmActivity = pvmTransition.getSource();

        if (!"userTask".equals(pvmActivity.getProperty("type"))) {
            logger.info("previous {} {} not userTask, just skip",
                    pvmActivity.getId(), pvmActivity.getProperty("type"));

            return;
        }

        String targetActivityId = pvmActivity.getId();

        /*
         * JdbcTemplate jdbcTemplate = ApplicationContextHelper .getBean(JdbcTemplate.class); String previousAssignee =
         * jdbcTemplate .queryForObject(
         * "select ASSIGNEE_ from ACT_HI_TASKINST where ACT_ID_=? order by END_TIME_ desc", String.class,
         * targetActivityId);
         */
        List<HistoricTaskInstanceEntity> historicTaskInstanceEntities = Context
                .getCommandContext().getDbSqlSession()
                .findInCache(HistoricTaskInstanceEntity.class);
        logger.info("{}", historicTaskInstanceEntities);

        String previousAssignee = null;

        for (HistoricTaskInstanceEntity historicTaskInstanceEntity : historicTaskInstanceEntities) {
            if (targetActivityId.equals(historicTaskInstanceEntity
                    .getTaskDefinitionKey())) {
                previousAssignee = historicTaskInstanceEntity.getAssignee();

                break;
            }
        }

        if (previousAssignee == null) {
            logger.info("cannot previous assignee, skip");

            return;
        }

        logger.info("previousAssignee : {}", previousAssignee);
        logger.info("delegateTask.getAssignee() : {}",
                delegateTask.getAssignee());

        if (previousAssignee.equals(delegateTask.getAssignee())) {
            logger.info("skip");

            new CompleteTaskWithCommentCmd(delegateTask.getId(),
                    Collections.<String, Object> emptyMap(), "相邻相同人员自动跳过")
                    .execute(Context.getCommandContext());
        }
    }

    public void processExpression(DelegateTask delegateTask, String value) {
        UserConnector userConnector = ApplicationContextHelper
                .getBean(UserConnector.class);
        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();
        String processInstanceId = delegateTask.getProcessInstanceId();
        HistoricProcessInstanceEntity historicProcessInstanceEntity = Context
                .getCommandContext().getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(processInstanceId);
        String initiator = historicProcessInstanceEntity.getStartUserId();
        MapVariableScope mapVariableScope = new MapVariableScope();
        mapVariableScope.setVariable("initiator",
                userConnector.findById(initiator));

        Object objectResult = expressionManager.createExpression(value)
                .getValue(mapVariableScope);

        if ((objectResult == null) || (!(objectResult instanceof Boolean))) {
            logger.error("{} is not Boolean, just return", objectResult);

            return;
        }

        Boolean result = (Boolean) objectResult;

        logger.info("value : {}, result : {}", value, result);

        if (result) {
            logger.info("skip task : {}", delegateTask.getId());
            new CompleteTaskWithCommentCmd(delegateTask.getId(),
                    Collections.<String, Object> emptyMap(), "跳过")
                    .execute(Context.getCommandContext());
        }
    }
}
