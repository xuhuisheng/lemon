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
@Component("com.mossle.party.component.UserCreatedSubscriber")
public class UserCreatedSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserCreatedSubscriber.class);
    private String insertPartyEntitySql = "INSERT INTO PARTY_ENTITY(NAME,REF,TYPE_ID) VALUES(?,?,?)";
    private String selectPartyEntitySql = "SELECT ID FROM PARTY_ENTITY WHERE REF=? AND TYPE_ID=?";
    private JsonMapper jsonMapper = new JsonMapper();
    private String destinationName = "queue.user.sync.created";
    private JdbcTemplate jdbcTemplate;

    public void handleMessage(String message) {
        try {
            UserDTO userDto = jsonMapper.fromJson(message, UserDTO.class);
            Long typeId = 1L;

            jdbcTemplate.update(insertPartyEntitySql, userDto.getUsername(),
                    userDto.getId(), typeId);

            logger.info("create user : {}", message);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
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
}
