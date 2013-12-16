package com.mossle.bpm.rule;

import com.mossle.core.spring.ApplicationContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class SuperiorAssigneeRule implements AssigneeRule {
    private static Logger logger = LoggerFactory
            .getLogger(SuperiorAssigneeRule.class);
    private JdbcTemplate jdbcTemplate;

    public String process(String username) {
        if (jdbcTemplate == null) {
            jdbcTemplate = (JdbcTemplate) ApplicationContextHolder
                    .getInstance().getApplicationContext()
                    .getBean("jdbcTemplate");
        }

        Long managerId = getManagerIdByUsername(username);

        return getUsername(managerId);
    }

    public Long getManagerIdByUsername(String username) {
        Long userId = getUserId(username);

        return getManagerId(userId, null);
    }

    public Long getManagerId(Long userId, Long departmentId) {
        Long targetDepartmentId = null;

        if (departmentId == null) {
            // 获得流程发起人的部门
            targetDepartmentId = this.getDepartmentId(userId);
        } else {
            targetDepartmentId = this.getHigherDepartmentId(departmentId);
        }

        logger.info("targetDepartmentId : {}", targetDepartmentId);

        if (targetDepartmentId == null) {
            return null;
        }

        // 获得部门负责人
        Long managerId = this.getManagerId(targetDepartmentId);
        logger.info("managerId : {}", managerId);

        // 如果部门没有负责人，或者负责人和发起人是一个人，就找上一个部门
        if ((managerId == null) || managerId.equals(userId)) {
            return this.getManagerId(userId, targetDepartmentId);
        } else {
            return managerId;
        }
    }

    // ~ ======================================================================
    public Long getDepartmentId(Long userId) {
        String sql = "select ps.parent_entity_id"
                + " from party_struct ps,party_entity parent,party_entity child,party_type parent_type,party_type child_type"
                + " where parent.id=ps.parent_entity_id and child.id=ps.child_entity_id"
                + " and parent_type.id=parent.type_id and parent_type.person<>1"
                + " and child_type.id=child.type_id and child_type.person=1 and ps.dim_id=1"
                + " and child.id=?";

        try {
            return jdbcTemplate.queryForObject(sql, Long.class, userId);
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);

            return null;
        }
    }

    public Long getManagerId(Long departmentId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select parent_entity_id from party_struct "
                            + "where dim_id=2 and child_entity_id=?",
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
                            + "where dim_id=1 and child_entity_id=?",
                    Long.class, departmentId);
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);

            return null;
        }
    }

    public String getUsername(Long userId) {
        Long id = jdbcTemplate.queryForObject(
                "select ref from party_entity where id=?", Long.class, userId);

        String username = jdbcTemplate.queryForObject(
                "select username from user_base where id=?", String.class, id);

        return username;
    }

    public Long getUserId(String username) {
        Long userId = jdbcTemplate.queryForObject(
                "select id from user_base where username=?", Long.class,
                username);

        Long partyEntityId = jdbcTemplate.queryForObject(
                "select id from party_entity where type_id=1 and ref=?",
                Long.class, userId);
        logger.info("username : {}, userId : {}", username, userId);

        return partyEntityId;
    }
}
