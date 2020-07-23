package com.mossle.api.user;

import java.util.Collections;
import java.util.List;

public class MockLocalUserConnector implements LocalUserConnector {
    public UserDTO findById(String userId, String userRepoRef) {
        return null;
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        return null;
    }

    public UserDTO updateAndFindById(String userId, String userRepoRef) {
        return null;
    }

    public UserDTO updateAndFindByUsername(String username, String userRepoRef) {
        return null;
    }

    public void createOrUpdateLocalUser(UserDTO userDto) {
    }

    public List<UserDTO> search(String query) {
        return Collections.emptyList();
    }
}
