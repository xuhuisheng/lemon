package com.mossle.api.userauth;

import java.util.Collections;

import javax.annotation.Resource;

import com.mossle.api.user.LocalUserConnector;
import com.mossle.api.user.UserDTO;

public class TestUserAuthConnector implements UserAuthConnector {
    private LocalUserConnector localUserConnector;

    public UserAuthDTO findByUsername(String username, String tenantId) {
        UserDTO userDto = localUserConnector.findByUsername(username, tenantId);

        return this.convertUserAuth(userDto);
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        throw new UnsupportedOperationException();
    }

    public UserAuthDTO findById(String id, String tenantId) {
        UserDTO userDto = localUserConnector.findById(id, tenantId);

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
    public void setLocalUserConnector(LocalUserConnector localUserConnector) {
        this.localUserConnector = localUserConnector;
    }
}
