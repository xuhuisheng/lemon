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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        UserConnector userConnector = ApplicationContextHelper
                .getBean(UserConnector.class);
        OrgConnector orgConnector = (OrgConnector) ApplicationContextHelper
                .getBean(OrgConnector.class);
        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();
        MapVariableScope mapVariableScope = new MapVariableScope();
        String initiator = historicProcessInstanceEntity.getStartUserId();
        mapVariableScope.setVariable("initiator",
                userConnector.findById(initiator));

        for (BpmConfRule bpmConfRule : bpmConfRules) {
            String value = bpmConfRule.getValue();

            if ("职位".equals(value)) {
                // 获得发起人的职位
                int initiatorLevel = orgConnector
                        .getJobLevelByUserId(initiator);

                // 获得审批人的职位
                int assigneeLevel = orgConnector
                        .getJobLevelByUserId(delegateTask.getAssignee());

                // 比较
                if (initiatorLevel >= assigneeLevel) {
                    logger.info("skip task : {}", delegateTask.getId());
                    logger.info("initiatorLevel : {}, assigneeLevel : {}",
                            initiatorLevel, assigneeLevel);
                    new CompleteTaskWithCommentCmd(delegateTask.getId(),
                            Collections.<String, Object> emptyMap(), "跳过")
                            .execute(Context.getCommandContext());
                }
            } else {
                Object objectResult = expressionManager.createExpression(value)
                        .getValue(mapVariableScope);

                if ((objectResult == null)
                        || (!(objectResult instanceof Boolean))) {
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
    }
}
