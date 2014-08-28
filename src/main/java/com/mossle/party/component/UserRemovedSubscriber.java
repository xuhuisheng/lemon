package com.mossle.party.component;

import java.io.IOException;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.ext.message.Subscribable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("com.mossle.party.component.UserRemovedSubscriber")
public class UserRemovedSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserRemovedSubscriber.class);
    private String removePartyStructSql = "DELETE FROM PARTY_STRUCT WHERE STRUCT_TYPE_ID=? AND PARENT_ENTITY_ID=? AND CHILD_ENTITY_ID=?";
    private String removePartyEntitySql = "DELETE FROM PARTY_ENTITY WHERE ID=?";
    private String selectPartyEntitySql = "SELECT ID FROM PARTY_ENTITY WHERE REF=? AND TYPE_ID=?";
    private JsonMapper jsonMapper = new JsonMapper();
    private String destinationName = "queue.user.sync.removed";
    private JdbcTemplate jdbcTemplate;

    public void handleMessage(String message) {
        try {
            UserDTO userDto = jsonMapper.fromJson(message, UserDTO.class);

            Long typeId = 1L;
            Long entityId = this.getPartyEntityId(userDto.getId(), typeId);

            if (entityId == null) {
                return;
            }

            Long structTypeId = 1L;
            jdbcTemplate.update(removePartyStructSql, structTypeId, entityId,
                    entityId);

            jdbcTemplate.update(removePartyEntitySql, entityId);

            logger.info("update user : {}", message);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public Long getPartyEntityId(String reference, Long typeId) {
        try {
            return jdbcTemplate.queryForObject(selectPartyEntitySql,
                    Long.class, reference, typeId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    // ~
    public boolean isTopic() {
        return false;
    }

    public String getName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
