package com.mossle.api.userauth;

import java.util.Collections;

public class MockUserAuthConnector implements UserAuthConnector {
    public UserAuthDTO findByUsername(String username, String tenantId) {
        return this.findByKey(username);
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        return this.findByKey(ref);
    }

    public UserAuthDTO findById(String id, String tenantId) {
        return this.findByKey(id);
    }

    public UserAuthDTO findByKey(String key) {
        UserAuthDTO userAuthDto = new UserAuthDTO();
        userAuthDto.setId(key);
        userAuthDto.setUsername(key);
        userAuthDto.setDisplayName(key);
        userAuthDto.setEnabled(true);
        userAuthDto.setPermissions(Collections.singletonList("*"));
        userAuthDto.setRoles(Collections.singletonList("ROLE_USER"));

        return userAuthDto;
    }
}
