package com.mossle.user.subscribe;

import java.io.IOException;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;
import com.mossle.api.userauth.UserAuthCache;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.subscribe.Subscribable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component("com.mossle.user.subscribe.UserRemovedSubscriber")
public class UserRemovedSubscriber implements Subscribable<String> {
    private static Logger logger = LoggerFactory
            .getLogger(UserRemovedSubscriber.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String destinationName = "topic.user.notify.removed";
    private UserCache userCache;
    private UserAuthCache userAuthCache;
    private TenantConnector tenantConnector;
    private UserAuthConnector userAuthConnector;

    public void handleMessage(String message) {
        try {
            UserDTO userDto = jsonMapper.fromJson(message, UserDTO.class);

            userCache.removeUser(userDto);

            for (TenantDTO tenantDto : tenantConnector.findAll()) {
                UserAuthDTO userAuthDto = userAuthConnector.findByUsername(
                        userDto.getUsername(), tenantDto.getId());
                userAuthCache.removeUserAuth(userAuthDto);
            }

            logger.info("remove user : {}", message);
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
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }

    @Resource
    public void setUserAuthConnector(UserAuthConnector userAuthConnector) {
        this.userAuthConnector = userAuthConnector;
    }
}
