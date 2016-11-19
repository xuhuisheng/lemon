package com.mossle.bpm.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.bpm.expr.Expr;
import com.mossle.bpm.expr.ExprProcessor;
import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class TaskConfTaskListener extends DefaultTaskListener implements
        ExprProcessor {
    private static Logger logger = LoggerFactory
            .getLogger(TaskConfTaskListener.class);
    private JdbcTemplate jdbcTemplate;

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        String businessKey = delegateTask.getExecution()
                .getProcessBusinessKey();
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();

        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();

        try {
            String sql = "select ASSIGNEE from BPM_TASK_CONF where BUSINESS_KEY=? and TASK_DEFINITION_KEY=?";
            String assignee = jdbcTemplate.queryForObject(sql, String.class,
                    businessKey, taskDefinitionKey);

            if ((assignee == null) || "".equals(assignee)) {
                return;
            }

            if ((assignee.indexOf("&&") != -1)
                    || (assignee.indexOf("||") != -1)) {
                logger.info("assignee : {}", assignee);

                List<String> candidateUsers = new Expr().evaluate(assignee,
                        this);
                logger.info("candidateUsers : {}", candidateUsers);
                delegateTask.addCandidateUsers(candidateUsers);
            } else {
                String value = expressionManager.createExpression(assignee)
                        .getValue(delegateTask).toString();
                delegateTask.setAssignee(value);
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }
    }

    public List<String> process(List<String> left, List<String> right,
            String operation) {
        if ("||".equals(operation)) {
            Set<String> set = new HashSet();
            set.addAll(left);
            set.addAll(right);

            return new ArrayList<String>(set);
        } else if ("&&".equals(operation)) {
            List<String> list = new ArrayList<String>();

            for (String username : left) {
                if (right.contains(username)) {
                    list.add(username);
                }
            }

            return list;
        } else {
            throw new UnsupportedOperationException(operation);
        }
    }

    public List<String> process(String text) {
        String sql = "select child.NAME from PARTY_ENTITY parent,PARTY_STRUCT ps,PARTY_ENTITY child,PARTY_TYPE child_type"
                + " where parent.ID=ps.PARENT_ENTITY_ID and ps.CHILD_ENTITY_ID=child.ID and child.TYPE_ID=child_type.ID"
                + " and child_type.PERSON=1 and parent.NAME=?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, text);
        List<String> usernames = new ArrayList<String>();

        for (Map<String, Object> map : list) {
            usernames.add(map.get("name").toString().toLowerCase());
        }

        logger.info("usernames : {}", usernames);

        return usernames;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
