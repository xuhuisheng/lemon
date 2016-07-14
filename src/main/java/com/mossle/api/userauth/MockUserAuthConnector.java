package com.mossle.api.userauth;

public class MockUserAuthConnector implements UserAuthConnector {
    public UserAuthDTO findByUsername(String username, String tenantId) {
        UserAuthDTO userAuthDto = new UserAuthDTO();
        userAuthDto.setId(username);
        userAuthDto.setUsername(username);
        userAuthDto.setDisplayName(username);
        userAuthDto.setEnabled(true);

        return userAuthDto;
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        UserAuthDTO userAuthDto = new UserAuthDTO();
        userAuthDto.setId(ref);
        userAuthDto.setUsername(ref);
        userAuthDto.setDisplayName(ref);
        userAuthDto.setEnabled(true);

        return userAuthDto;
    }

    public UserAuthDTO findById(String id, String tenantId) {
        UserAuthDTO userAuthDto = new UserAuthDTO();
        userAuthDto.setId(id);
        userAuthDto.setUsername(id);
        userAuthDto.setDisplayName(id);
        userAuthDto.setEnabled(true);

        return userAuthDto;
    }
}
