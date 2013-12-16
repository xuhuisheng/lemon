package com.mossle.bpm.listener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.bpm.rule.AssigneeRule;
import com.mossle.bpm.rule.SuperiorAssigneeRule;
import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class AssigneeAliasTaskListener extends DefaultTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(AssigneeAliasTaskListener.class);
    private JdbcTemplate jdbcTemplate;
    private Map<String, AssigneeRule> assigneeRuleMap = new HashMap<String, AssigneeRule>();

    public AssigneeAliasTaskListener() {
        assigneeRuleMap.put("部门经理", new SuperiorAssigneeRule());
    }

    @Override
    public void onAssignment(DelegateTask delegateTask) throws Exception {
        String assignee = delegateTask.getAssignee();
        AssigneeRule assigneeRule = assigneeRuleMap.get(assignee);

        if (assigneeRule != null) {
            String processInstanceId = delegateTask.getProcessInstanceId();
            String username = Context.getCommandContext()
                    .getHistoricProcessInstanceEntityManager()
                    .findHistoricProcessInstance(processInstanceId)
                    .getStartUserId();

            delegateTask.setAssignee(assigneeRule.process(username));
        }
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
