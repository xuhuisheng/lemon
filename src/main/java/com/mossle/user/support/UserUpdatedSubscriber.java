package com.mossle.user.support;

import java.io.IOException;

import javax.annotation.Resource;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeDTO;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;
import com.mossle.api.userauth.UserAuthCache;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.ext.message.Subscribable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class UserUpdatedSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserUpdatedSubscriber.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String destinationName = "topic.user.notify.updated";
    private UserCache userCache;
    private UserAuthCache userAuthCache;
    private ScopeConnector scopeConnector;
    private UserAuthConnector userAuthConnector;

    public void handleMessage(String message) {
        try {
            UserDTO userDto = jsonMapper.fromJson(message, UserDTO.class);

            userCache.updateUser(userDto);

            for (ScopeDTO scopeDto : scopeConnector.findAll()) {
                UserAuthDTO userAuthDto = userAuthConnector.findByUsername(
                        userDto.getUsername(), scopeDto.getId());
                userAuthCache.updateUserAuth(userAuthDto);
            }

            logger.info("update user : {}", message);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public boolean isTopic() {
        return true;
    }

    public String getName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    @Resource
    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    @Resource
    public void setUserAuthCache(UserAuthCache userAuthCache) {
        this.userAuthCache = userAuthCache;
    }

    @Resource
    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    @Resource
    public void setUserAuthConnector(UserAuthConnector userAuthConnector) {
        this.userAuthConnector = userAuthConnector;
    }
}
