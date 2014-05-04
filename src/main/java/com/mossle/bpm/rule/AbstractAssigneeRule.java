package com.mossle.bpm.rule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mossle.core.spring.ApplicationContextHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 通用的抽象工具基类.
 * 
 * @todo 这里很多的逻辑都应该移动到orgConnector里
 */
public abstract class AbstractAssigneeRule implements AssigneeRule {
    private static Logger logger = LoggerFactory
            .getLogger(AbstractAssigneeRule.class);
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate != null) {
            return jdbcTemplate;
        }

        jdbcTemplate = ApplicationContextHelper.getBean(JdbcTemplate.class);

        return jdbcTemplate;
    }

    /**
     * 根据user对应的partyEntityId查找直接领导的partyEntityId.
     */
    public String getManagerEntityIdByUserEntityId(String userEntityId) {
        return getManagerEntityId(userEntityId, null);
    }

    /**
     * 根据user对应的partyEntityId查找直接领导的partyEntityId. 如果找不到，继续按照department向上递归
     */
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
    /**
     * 根据人员partyEntityId获得所属部门的partyEntityId.
     */
    public String getDepartmentEntityId(String userEntityId) {
        // 不要理岗位，只查组织机构（公司，部门，小组）
        String sql = "select ps.parent_entity_id as id"
                + " from party_struct ps,party_entity parent,party_entity child,party_type parent_type,party_type child_type"
                + " where parent.id=ps.parent_entity_id and child.id=ps.child_entity_id"
                + " and parent_type.id=parent.type_id and parent_type.type=0"
                + " and child_type.id=child.type_id and child_type.type=1"
                + " and child.id=?";

        try {
            List<String> list = this.getJdbcTemplate().queryForList(sql,
                    String.class, userEntityId);

            if (list.isEmpty()) {
                return null;
            }

            return list.get(0);
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);

            return null;
        }
    }

    /**
     * 根据部门的partyEntityId获得部门负责人的partyEntityId.
     */
    public String getManagerEntityId(String departmentEntityId) {
        try {
            return this.getJdbcTemplate().queryForObject(
                    "select child_entity_id from party_struct "
                            + "where admin=1 and parent_entity_id=?",
                    String.class, departmentEntityId);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);

            return null;
        }
    }

    /**
     * 根据部门partyEntityId获得上级部门的partyEntityId.
     */
    public String getHigherDepartmentEntityId(String departmentEntityId) {
        try {
            return this.getJdbcTemplate().queryForObject(
                    "select parent_entity_id from party_struct "
                            + "where child_entity_id=?", String.class,
                    departmentEntityId);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);

            return null;
        }
    }

    // ~ ======================================================================
    /**
     * partyEntityId转换成userId.
     */
    public String getUserId(String userEntityId) {
        try {
            String userId = this.getJdbcTemplate().queryForObject(
                    "select ref from party_entity where id=?", String.class,
                    userEntityId);

            return userId;
        } catch (EmptyResultDataAccessException ex) {
            logger.error("cannot find userId for partyEntityId : {}",
                    userEntityId);
            logger.debug(ex.getMessage(), ex);

            return null;
        }
    }

    /**
     * userId转换成partyEntity的id.
     */
    public String getUserEntityId(String userId) {
        String partyEntityId = this.getJdbcTemplate().queryForObject(
                "select pe.id from party_entity pe,party_type pt"
                        + " where pe.type_id=pt.id and pt.type=1 and pe.ref=?",
                String.class, userId);
        logger.debug("userId : {}", userId);

        return partyEntityId;
    }
}
