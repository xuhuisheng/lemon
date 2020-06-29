package com.mossle.api.user;

import java.util.Collections;
import java.util.List;

public class TestRemoteUserConnector implements RemoteUserConnector {
    public UserDTO findById(String userId, String userRepoRef) {
        UserDTO userDto = new UserDTO();
        userDto.setCode(userId);
        userDto.setUsername(userId);
        userDto.setDisplayName(userId);

        return userDto;
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        UserDTO userDto = new UserDTO();
        userDto.setId(username);
        userDto.setCode(username);
        userDto.setUsername(username);
        userDto.setDisplayName(username);

        return userDto;
    }

    public boolean authenticate(String username, String password) {
        return true;
    }

    public List<UserDTO> search(String query) {
        String username = query;
        UserDTO userDto = new UserDTO();
        userDto.setId(username);
        userDto.setCode(username);
        userDto.setUsername(username);
        userDto.setDisplayName(username);

        return Collections.singletonList(userDto);
    }
}
