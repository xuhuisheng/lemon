package com.mossle.client.user;

import java.util.Collections;
import java.util.List;

import com.mossle.api.user.UserDTO;

public class MockUserClient implements UserClient {
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

    public String convertAlias(String alias, String userRepoRef) {
        if (alias == null) {
            throw new IllegalStateException(alias);
        }

        return alias.trim().toLowerCase();
    }

    public List<UserDTO> search(String query) {
        return Collections.emptyList();
    }
}
