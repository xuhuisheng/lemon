package com.mossle.auth.component;

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
@Component("com.mossle.auth.component.UserCreatedSubscriber")
public class UserCreatedSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserCreatedSubscriber.class);
    private String insertSql = "INSERT INTO AUTH_USER_STATUS(USERNAME,REF,STATUS,PASSWORD,USER_REPO_REF,SCOPE_ID) VALUES(?,?,1,'','1','1')";
    private JsonMapper jsonMapper = new JsonMapper();
    private String destinationName = "queue.user.sync.created";
    private JdbcTemplate jdbcTemplate;

    public void handleMessage(String message) {
        try {
            UserDTO userDto = jsonMapper.fromJson(message, UserDTO.class);
            // Long typeId = 1L;
            jdbcTemplate.update(insertSql, userDto.getUsername(),
                    userDto.getId());

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
