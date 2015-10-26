package com.mossle.humantask.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.rule.AssigneeRule;
import com.mossle.humantask.rule.PositionAssigneeRule;
import com.mossle.humantask.rule.RuleMatcher;
import com.mossle.humantask.rule.SuperiorAssigneeRule;

import com.mossle.spi.process.InternalProcessConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssigneeAliasHumanTaskListener implements HumanTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(AssigneeAliasHumanTaskListener.class);
    private InternalProcessConnector internalProcessConnector;
    private Map<RuleMatcher, AssigneeRule> assigneeRuleMap = new HashMap<RuleMatcher, AssigneeRule>();

    public AssigneeAliasHumanTaskListener() {
        SuperiorAssigneeRule superiorAssigneeRule = new SuperiorAssigneeRule();
        PositionAssigneeRule positionAssigneeRule = new PositionAssigneeRule();
        assigneeRuleMap.put(new RuleMatcher("常用语"), superiorAssigneeRule);
        assigneeRuleMap.put(new RuleMatcher("岗位"), positionAssigneeRule);
    }

    @Override
    public void onCreate(TaskInfo taskInfo) throws Exception {
        String assignee = taskInfo.getAssignee();
        logger.debug("assignee : {}", assignee);

        if (assignee == null) {
            return;
        }

        if (assignee.startsWith("${")) {
            assignee = (String) internalProcessConnector.executeExpression(
                    taskInfo.getTaskId(), assignee);
            taskInfo.setAssignee(assignee);

            return;
        }

        for (Map.Entry<RuleMatcher, AssigneeRule> entry : assigneeRuleMap
                .entrySet()) {
            RuleMatcher ruleMatcher = entry.getKey();

            if (!ruleMatcher.matches(assignee)) {
                continue;
            }

            String value = ruleMatcher.getValue(assignee);
            AssigneeRule assigneeRule = entry.getValue();
            logger.debug("value : {}", value);
            logger.debug("assigneeRule : {}", assigneeRule);

            if (assigneeRule instanceof SuperiorAssigneeRule) {
                this.processSuperior(taskInfo, assigneeRule, value);
            } else if (assigneeRule instanceof PositionAssigneeRule) {
                this.processPosition(taskInfo, assigneeRule, value);
            }
        }
    }

    @Override
    public void onComplete(TaskInfo taskInfo) throws Exception {
    }

    public void processSuperior(TaskInfo taskInfo, AssigneeRule assigneeRule,
            String value) {
        String processInstanceId = taskInfo.getProcessInstanceId();
        String startUserId = internalProcessConnector
                .findInitiator(processInstanceId);
        String userId = assigneeRule.process(startUserId);
        logger.debug("userId : {}", userId);
        taskInfo.setAssignee(userId);
    }

    public void processPosition(TaskInfo taskInfo, AssigneeRule assigneeRule,
            String value) {
        String processInstanceId = taskInfo.getProcessInstanceId();
        String startUserId = internalProcessConnector
                .findInitiator(processInstanceId);
        List<String> userIds = assigneeRule.process(value, startUserId);
        logger.debug("userIds : {}", userIds);

        if (!userIds.isEmpty()) {
            taskInfo.setAssignee(userIds.get(0));
        }
    }

    @Resource
    public void setInternalProcessConnector(
            InternalProcessConnector internalProcessConnector) {
        this.internalProcessConnector = internalProcessConnector;
    }
}
