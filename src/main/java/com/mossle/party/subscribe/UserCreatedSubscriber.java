package com.mossle.party.subscribe;

import java.io.IOException;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

import com.mossle.core.id.IdGenerator;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.subscribe.Subscribable;

import com.mossle.party.PartyConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("com.mossle.party.subscribe.UserCreatedSubscriber")
public class UserCreatedSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserCreatedSubscriber.class);
    private String insertPartyEntitySql = "INSERT INTO PARTY_ENTITY(ID,NAME,REF,TYPE_ID,TENANT_ID) VALUES(?,?,?,?,?)";
    private String selectUserPartyTypeSql = "SELECT ID FROM PARTY_TYPE WHERE TYPE=? AND TENANT_ID=?";
    private JsonMapper jsonMapper = new JsonMapper();
    private String destinationName = "queue.user.sync.created";
    private JdbcTemplate jdbcTemplate;
    private IdGenerator idGenerator;

    public void handleMessage(String message) {
        logger.info("create party user : {}", message);

        try {
            UserDTO userDto = jsonMapper.fromJson(message, UserDTO.class);
            String tenantId = userDto.getUserRepoRef();
            Long typeId = this.findUserTypeId(tenantId);

            jdbcTemplate.update(insertPartyEntitySql, idGenerator.generateId(),
                    userDto.getUsername(), userDto.getId(), typeId, tenantId);

            logger.info("create user : {}", message);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public Long findUserTypeId(String tenantId) {
        Long typeId = jdbcTemplate.queryForObject(selectUserPartyTypeSql,
                Long.class, PartyConstants.TYPE_USER, tenantId);

        return typeId;
    }

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

    @Resource
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
