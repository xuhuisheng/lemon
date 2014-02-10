package com.mossle.bpm.rule;

import com.mossle.core.spring.ApplicationContextHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class SuperiorAssigneeRule implements AssigneeRule {
    private static Logger logger = LoggerFactory
            .getLogger(SuperiorAssigneeRule.class);
    private JdbcTemplate jdbcTemplate;

    public String process(String userId) {
        if (jdbcTemplate == null) {
            jdbcTemplate = ApplicationContextHelper.getBean(JdbcTemplate.class);
        }

        String userEntityId = getUserEntityId(userId);
        String managerEntityId = getManagerEntityIdByUserEntityId(userEntityId);

        return getUserId(managerEntityId);
    }

    public String getManagerEntityIdByUserEntityId(String userEntityId) {
        return getManagerEntityId(userEntityId, null);
    }

    public String getManagerEntityId(String userEntityId,
            String departmentEntityId) {
        String targetDepartmentEntityId = null;

        if (departmentEntityId == null) {
            // 获得流程发起人的部门
            targetDepartmentEntityId = this.getDepartmentEntityId(userEntityId);
        } else {
            targetDepartmentEntityId = this
                    .getHigherDepartmentEntityId(departmentEntityId);
        }

        logger.debug("targetDepartmentEntityId : {}", targetDepartmentEntityId);

        if (targetDepartmentEntityId == null) {
            return null;
        }

        // 获得部门负责人
        String managerEntityId = this
                .getManagerEntityId(targetDepartmentEntityId);
        logger.debug("managerEntityId : {}", managerEntityId);

        // 如果部门没有负责人，或者负责人和发起人是一个人，就找上一个部门
        if ((managerEntityId == null) || managerEntityId.equals(userEntityId)) {
            return this.getManagerEntityId(userEntityId,
                    targetDepartmentEntityId);
        } else {
            return managerEntityId;
        }
    }

    // ~ ======================================================================
    public String getDepartmentEntityId(String userEntityId) {
        String sql = "select ps.parent_entity_id"
                + " from party_struct ps,party_entity parent,party_entity child,party_type parent_type,party_type child_type"
                + " where parent.id=ps.parent_entity_id and child.id=ps.child_entity_id"
                + " and parent_type.id=parent.type_id and parent_type.person<>1"
                + " and child_type.id=child.type_id and child_type.person=1 and ps.dim_id=1"
                + " and child.id=?";

        try {
            return jdbcTemplate.queryForObject(sql, String.class, userEntityId);
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);

            return null;
        }
    }

    public String getManagerEntityId(String departmentEntityId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select parent_entity_id from party_struct "
                            + "where dim_id=2 and child_entity_id=?",
                    String.class, departmentEntityId);
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);

            return null;
        }
    }

    public String getHigherDepartmentEntityId(String departmentEntityId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select parent_entity_id from party_struct "
                            + "where dim_id=1 and child_entity_id=?",
                    String.class, departmentEntityId);
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);

            return null;
        }
    }

    public String getUserId(String userEntityId) {
        String userId = jdbcTemplate.queryForObject(
                "select ref from party_entity where id=?", String.class,
                userEntityId);

        return userId;
    }

    public String getUserEntityId(String userId) {
        String partyEntityId = jdbcTemplate.queryForObject(
                "select id from party_entity where type_id=1 and ref=?",
                String.class, userId);
        logger.debug("userId : {}", userId);

        return partyEntityId;
    }
}
