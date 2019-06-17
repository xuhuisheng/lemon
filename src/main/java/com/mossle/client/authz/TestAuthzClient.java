package com.mossle.client.authz;

import java.util.Collections;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.client.user.UserClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAuthzClient implements AuthzClient {
    private static Logger logger = LoggerFactory
            .getLogger(TestAuthzClient.class);
    private UserClient userClient;

    public UserAuthDTO findByUsername(String username, String tenantId) {
        logger.info("findByUsername : {}", username);

        UserDTO userDto = userClient.findByUsername(username, tenantId);

        return this.convertUserAuth(userDto);
    }

    public UserAuthDTO findById(String id, String tenantId) {
        logger.info("findById : {}", id);

        UserDTO userDto = userClient.findById(id, tenantId);

        return this.convertUserAuth(userDto);
    }

    public UserAuthDTO convertUserAuth(UserDTO userDto) {
        UserAuthDTO userAuthDto = new UserAuthDTO();
        userAuthDto.setId(userDto.getId());
        userAuthDto.setUsername(userDto.getUsername());
        userAuthDto.setDisplayName(userDto.getDisplayName());
        userAuthDto.setEnabled(true);
        userAuthDto.setPermissions(Collections.singletonList("*"));
        userAuthDto.setRoles(Collections.singletonList("ROLE_USER"));

        return userAuthDto;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
