package com.mossle.bpm.listener;

import javax.annotation.Resource;

import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class TaskConfTaskListener extends DefaultTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(TaskConfTaskListener.class);
    private JdbcTemplate jdbcTemplate;

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        String businessKey = delegateTask.getExecution()
                .getProcessBusinessKey();
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();

        try {
            String sql = "select assignee from bpm_task_conf where business_key=? and task_definition_key=?";
            String assignee = jdbcTemplate.queryForObject(sql, String.class,
                    businessKey, taskDefinitionKey);
            delegateTask.setAssignee(assignee);
        } catch (Exception ex) {
        }
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
