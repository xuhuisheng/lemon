package com.mossle.auth.subscribe;

import java.io.IOException;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.subscribe.Subscribable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("com.mossle.auth.component.UserRemovedSubscriber")
public class UserRemovedSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserRemovedSubscriber.class);
    private String removeUserRoleSql = "DELETE FROM AUTH_USER_ROLE WHERE USER_STATUS_ID=?";
    private String removeUserSql = "DELETE FROM AUTH_USER_STATUS WHERE ID=?";
    private String selectUserSql = "SELECT ID FROM AUTH_USER_STATUS WHERE REF=? AND TENANT_ID=?";
    private JsonMapper jsonMapper = new JsonMapper();
    private String destinationName = "queue.user.sync.removed";
    private JdbcTemplate jdbcTemplate;

    public void handleMessage(String message) {
        try {
            UserDTO userDto = jsonMapper.fromJson(message, UserDTO.class);
            String tenantId = userDto.getUserRepoRef();

            Long entityId = this.getAuthUserStatusId(userDto.getId(), tenantId);

            if (entityId == null) {
                return;
            }

            jdbcTemplate.update(removeUserRoleSql, entityId);

            jdbcTemplate.update(removeUserSql, entityId);

            logger.info("update user : {}", message);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public Long getAuthUserStatusId(String ref, String tenantId) {
        try {
            return jdbcTemplate.queryForObject(selectUserSql, Long.class, ref,
                    tenantId);
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
