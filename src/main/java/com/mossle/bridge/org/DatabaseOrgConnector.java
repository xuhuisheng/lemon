package com.mossle.bridge.org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.org.OrgConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseOrgConnector implements OrgConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseOrgConnector.class);
    private JdbcTemplate jdbcTemplate;

    public int getJobLevelByUserId(String userId) {
        String sql = "select jl.name from job_level jl,job_info ji,job_user ju"
                + " where ju.user_ref=? and ju.job_info_id=ji.id and ji.level_id=jl.id";

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, userId);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);

            return -1;
        }
    }

    public int getJobLevelByInitiatorAndPosition(String initiator,
            String positionName) {
        String userEntityId = this.getUserEntityId(initiator);
        String departmentId = this.getDepartmentEntityId(userEntityId);
        List<String> userEntityIds = this.getPositionUserEntityIds(
                departmentId, positionName);
        List<String> userIds = new ArrayList<String>();

        for (String uId : userEntityIds) {
            userIds.add(getUserId(uId));
        }

        if (userIds.isEmpty()) {
            return -1;
        }

        String userId = userIds.get(0);

        return getJobLevelByUserId(userId);
    }

    public List<String> getPositionUserEntityIds(String departmentId,
            String positionName) {
        String sql = "select ps.child_entity_id from party_struct ps,party_entity user,party_type pt,"
                + " job_user ju,job_info ji,job_title jt"
                + " where ps.parent_entity_id=? and ps.child_entity_id=user.id and user.type_id=pt.id"
                + " and pt.type=1 and user.ref=ju.user_ref and ju.job_info_id=ji.id and ji.title_id=jt.id and jt.name=?";
        List<String> userIds = jdbcTemplate.queryForList(sql, String.class,
                departmentId, positionName);
        logger.info("departmentId : {}, positionName : {}", departmentId,
                positionName);
        logger.info("userIds : {}", userIds);

        if (userIds.isEmpty()) {
            String higherDepartmentId = getHigherDepartmentEntityId(departmentId);

            if (higherDepartmentId == null) {
                return Collections.emptyList();
            }

            return getPositionUserEntityIds(higherDepartmentId, positionName);
        }

        return userIds;
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
            List<String> list = jdbcTemplate.queryForList(sql, String.class,
                    userEntityId);

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
     * 根据部门partyEntityId获得上级部门的partyEntityId.
     */
    public String getHigherDepartmentEntityId(String departmentEntityId) {
        try {
            return jdbcTemplate.queryForObject(
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
            String userId = jdbcTemplate.queryForObject(
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
        String partyEntityId = jdbcTemplate.queryForObject(
                "select pe.id from party_entity pe,party_type pt"
                        + " where pe.type_id=pt.id and pt.type=1 and pe.ref=?",
                String.class, userId);
        logger.debug("userId : {}", userId);

        return partyEntityId;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
