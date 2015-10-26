package com.mossle.party.subscribe;

import java.io.IOException;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.subscribe.Subscribable;

import com.mossle.party.PartyConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("com.mossle.party.subscribe.UserRemovedSubscriber")
public class UserRemovedSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserRemovedSubscriber.class);
    private String removePartyStructSql = "DELETE FROM PARTY_STRUCT WHERE PARENT_ENTITY_ID=? AND CHILD_ENTITY_ID=?";
    private String removePartyEntitySql = "DELETE FROM PARTY_ENTITY WHERE ID=?";
    private String selectPartyEntitySql = "SELECT ID FROM PARTY_ENTITY WHERE REF=? AND TYPE_ID=? AND TENANT_ID=?";
    private String selectUserPartyTypeSql = "SELECT ID FROM PARTY_TYPE WHERE TYPE=? AND TENANT_ID=?";
    private JsonMapper jsonMapper = new JsonMapper();
    private String destinationName = "queue.user.sync.removed";
    private JdbcTemplate jdbcTemplate;

    public void handleMessage(String message) {
        logger.info("remove party user : {}", message);

        try {
            UserDTO userDto = jsonMapper.fromJson(message, UserDTO.class);
            String tenantId = userDto.getUserRepoRef();
            Long typeId = this.findUserTypeId(tenantId);
            Long entityId = this.getPartyEntityId(userDto.getId(), typeId,
                    tenantId);

            if (entityId == null) {
                return;
            }

            jdbcTemplate.update(removePartyStructSql, entityId, entityId);

            jdbcTemplate.update(removePartyEntitySql, entityId);

            logger.info("remove user : {}", message);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public Long findUserTypeId(String tenantId) {
        Long typeId = jdbcTemplate.queryForObject(selectUserPartyTypeSql,
                Long.class, PartyConstants.TYPE_USER, tenantId);

        return typeId;
    }

    public Long getPartyEntityId(String reference, Long typeId, String tenantId) {
        try {
            return jdbcTemplate.queryForObject(selectPartyEntitySql,
                    Long.class, reference, typeId, tenantId);
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
