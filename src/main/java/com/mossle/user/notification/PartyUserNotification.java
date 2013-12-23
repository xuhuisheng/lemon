package com.mossle.user.notification;

import javax.annotation.Resource;

import com.mossle.user.persistence.domain.UserBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class PartyUserNotification implements UserNotification {
    private static Logger logger = LoggerFactory
            .getLogger(PartyUserNotification.class);
    private JdbcTemplate jdbcTemplate;

    // insert
    private String insertPartyEntitySql = "insert into party_entity(name,ref,type_id) values(?,?,?)";
    private String selectPartyEntitySql = "select id from party_entity where ref=? and type_id=?";

    // update
    private String updatePartyEntitySql = "update party_entity set name=? where ref=? and type_id=?";

    // remove
    private String removePartyStructSql = "delete from party_struct where STRUCT_TYPE_ID=? and PARENT_ENTITY_ID=? and CHILD_ENTITY_ID=?";
    private String removePartyEntitySql = "delete from party_entity where id=?";

    public void insertUser(UserBase userBase) {
        Long typeId = 1L;

        jdbcTemplate.update(insertPartyEntitySql, userBase.getUsername(),
                userBase.getId(), typeId);
    }

    public void updateUser(UserBase userBase) {
        Long typeId = 1L;
        jdbcTemplate.update(updatePartyEntitySql, userBase.getUsername(),
                userBase.getId(), typeId);
    }

    public void removeUser(UserBase userBase) {
        Long typeId = 1L;
        Long entityId = this.getPartyEntityId(userBase.getId(), typeId);

        if (entityId == null) {
            return;
        }

        Long structTypeId = 1L;
        jdbcTemplate.update(removePartyStructSql, structTypeId, entityId,
                entityId);

        jdbcTemplate.update(removePartyEntitySql, entityId);
    }

    public Long getPartyEntityId(Long reference, Long typeId) {
        try {
            return jdbcTemplate.queryForObject(selectPartyEntitySql,
                    Long.class, reference, typeId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
