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

import com.mossle.core.mapper.BeanMapper;

import com.mossle.spi.process.InternalProcessConnector;
import com.mossle.spi.process.ParticipantDefinition;
import com.mossle.spi.process.ProcessTaskDefinition;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class HumanTaskUserTaskListener extends DefaultTaskListener implements
        ExprProcessor {
    private static Logger logger = LoggerFactory
            .getLogger(HumanTaskUserTaskListener.class);
    private InternalProcessConnector internalProcessConnector;
    private BeanMapper beanMapper = new BeanMapper();
    private JdbcTemplate jdbcTemplate;

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        String processDefinitionId = delegateTask.getProcessDefinitionId();
        String businessKey = delegateTask.getExecution()
                .getProcessBusinessKey();
        String taskDefinitionKey = delegateTask.getExecution()
                .getCurrentActivityId();
        ProcessTaskDefinition processTaskDefinition = internalProcessConnector
                .findTaskDefinition(processDefinitionId, businessKey,
                        taskDefinitionKey);
        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();

        for (ParticipantDefinition participantDefinition : processTaskDefinition
                .getParticipantDefinitions()) {
            if ("user".equals(participantDefinition.getType())) {
                if ("add".equals(participantDefinition.getStatus())) {
                    delegateTask.addCandidateUser(participantDefinition
                            .getValue());
                } else {
                    delegateTask.deleteCandidateUser(participantDefinition
                            .getValue());
                }
            } else {
                if ("add".equals(participantDefinition.getStatus())) {
                    delegateTask.addCandidateGroup(participantDefinition
                            .getValue());
                } else {
                    delegateTask.deleteCandidateGroup(participantDefinition
                            .getValue());
                }
            }
        }

        String assignee = null;

        if (processTaskDefinition.getAssignee() != null) {
            assignee = expressionManager
                    .createExpression(processTaskDefinition.getAssignee())
                    .getValue(delegateTask).toString();
        }

        if (assignee == null) {
            delegateTask.setAssignee(null);
        } else if ((assignee.indexOf("&&") != -1)
                || (assignee.indexOf("||") != -1)) {
            logger.debug("assignee : {}", assignee);

            List<String> candidateUsers = new Expr().evaluate(assignee, this);
            logger.debug("candidateUsers : {}", candidateUsers);
            delegateTask.addCandidateUsers(candidateUsers);
        } else {
            delegateTask.setAssignee(assignee);
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
    public void setInternalProcessConnector(
            InternalProcessConnector internalProcessConnector) {
        this.internalProcessConnector = internalProcessConnector;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
