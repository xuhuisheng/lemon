package com.mossle.auth.subscribe;

import java.io.IOException;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

import com.mossle.auth.component.AuthCache;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.persistence.manager.UserStatusManager;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.subscribe.Subscribable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("com.mossle.auth.component.UserUpdatedSubscriber")
public class UserUpdatedSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserUpdatedSubscriber.class);
    private String updateSql = "UPDATE AUTH_USER_STATUS SET USERNAME=? WHERE REF=? AND TENANT_ID=?";
    private JsonMapper jsonMapper = new JsonMapper();
    private String destinationName = "queue.user.sync.updated";
    private JdbcTemplate jdbcTemplate;
    private UserStatusManager userStatusManager;
    private AuthCache authCache;

    public void handleMessage(String message) {
        UserDTO userDto = null;
        String tenantId = null;

        try {
            userDto = jsonMapper.fromJson(message, UserDTO.class);

            tenantId = userDto.getUserRepoRef();
            jdbcTemplate.update(updateSql, userDto.getUsername(),
                    userDto.getId(), tenantId);

            logger.info("update user : {}", message);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        if (userDto != null) {
            String hql = "from UserStatus where username=? and tenantId=?";
            UserStatus userStatus = userStatusManager.findUnique(hql,
                    userDto.getUsername(), tenantId);

            if (userStatus != null) {
                authCache.evictUserStatus(userStatus);
                logger.info("refresh cache : {}, {}", userStatus.getUsername(),
                        userStatus.getTenantId());
            } else {
                authCache.evictUser(userDto.getId());
                logger.info("cannot find cache : {}", userDto.getUsername());
            }
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

    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    @Resource
    public void setAuthCache(AuthCache authCache) {
        this.authCache = authCache;
    }
}
