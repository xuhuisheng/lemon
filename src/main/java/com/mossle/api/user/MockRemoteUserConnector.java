package com.mossle.api.user;

import java.util.Collections;
import java.util.List;

public class MockRemoteUserConnector implements RemoteUserConnector {
    public UserDTO findById(String userId, String userRepoRef) {
        return null;
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        return null;
    }

    public boolean authenticate(String username, String password) {
        return false;
    }

    public List<UserDTO> search(String query) {
        return Collections.emptyList();
    }
}
