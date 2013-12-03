package com.mossle.bpm.listener;

import javax.annotation.Resource;

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

    @Override
    public void onAssignment(DelegateTask delegateTask) throws Exception {
        String assignee = delegateTask.getAssignee();

        if ("部门经理".equals(assignee)) {
            String processInstanceId = delegateTask.getProcessInstanceId();
            String username = Context.getCommandContext()
                    .getHistoricProcessInstanceEntityManager()
                    .findHistoricProcessInstance(processInstanceId)
                    .getStartUserId();
            Long userId = this.getUserId(username);
            Long managerId = this.getManagerId(userId, null);

            if (managerId == null) {
                throw new IllegalStateException(
                        "cannot find manager for user : " + username);
            }

            delegateTask.setAssignee(this.getUsername(managerId));
        }
    }

    public Long getManagerId(Long userId, Long departmentId) {
        Long targetDepartmentId = null;

        if (departmentId == null) {
            // 获得流程发起人的部门
            targetDepartmentId = this.getDepartmentId(userId);
        } else {
            targetDepartmentId = this.getHigherDepartmentId(departmentId);
        }

        if (targetDepartmentId == null) {
            return null;
        }

        // 获得部门负责人
        Long managerId = this.getManagerId(targetDepartmentId);

        // 如果部门没有负责人，或者负责人和发起人是一个人，就找上一个部门
        if ((managerId == null) || managerId.equals(userId)) {
            return this.getManagerId(userId, targetDepartmentId);
        } else {
            return managerId;
        }
    }

    // ~ ======================================================================
    public Long getDepartmentId(Long userId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select parent_entity_id from party_struct "
                            + "where struct_type_id=1 and child_entity_id=?",
                    Long.class, userId);
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);

            return null;
        }
    }

    public Long getManagerId(Long departmentId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select parent_entity_id from party_struct "
                            + "where struct_type_id=2 and child_entity_id=?",
                    Long.class, departmentId);
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);

            return null;
        }
    }

    public Long getHigherDepartmentId(Long departmentId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select parent_entity_id from party_struct "
                            + "where struct_type_id=1 and child_entity_id=?",
                    Long.class, departmentId);
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);

            return null;
        }
    }

    public String getUsername(Long userId) {
        Long id = jdbcTemplate.queryForObject(
                "select reference from party_entity where id=?", Long.class,
                userId);

        return jdbcTemplate.queryForObject(
                "select username from user_base where id=?", String.class, id);
    }

    public Long getUserId(String username) {
        Long userId = jdbcTemplate.queryForObject(
                "select id from user_base where username=?", Long.class,
                username);

        return jdbcTemplate.queryForObject(
                "select id from party_entity where type_id=1 and reference=?",
                Long.class, userId);
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
