package com.mossle.user.notification;

import javax.annotation.Resource;

import com.mossle.core.id.IdGenerator;

import com.mossle.user.persistence.domain.UserBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class PartyUserNotification implements UserNotification {
    private static Logger logger = LoggerFactory
            .getLogger(PartyUserNotification.class);
    private JdbcTemplate jdbcTemplate;
    private IdGenerator idGenerator;

    // insert
    private String insertPartyEntitySql = "INSERT INTO PARTY_ENTITY(ID,NAME,REF,TYPE_ID) VALUES(?,?,?,?)";
    private String selectPartyEntitySql = "SELECT ID FROM PARTY_ENTITY WHERE REF=? AND TYPE_ID=?";

    // update
    private String updatePartyEntitySql = "UPDATE PARTY_ENTITY SET NAME=? WHERE REF=? AND TYPE_ID=?";

    // remove
    private String removePartyStructSql = "DELETE FROM PARTY_STRUCT WHERE STRUCT_TYPE_ID=? AND PARENT_ENTITY_ID=? AND CHILD_ENTITY_ID=?";
    private String removePartyEntitySql = "DELETE FROM PARTY_ENTITY WHERE ID=?";

    public void insertUser(UserBase userBase) {
        Long typeId = 1L;

        jdbcTemplate.update(insertPartyEntitySql, idGenerator.generateId(),
                userBase.getUsername(), userBase.getId(), typeId);
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

    @Resource
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
