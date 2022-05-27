package com.mossle.client.user;

import java.util.Collections;
import java.util.List;

import com.mossle.api.user.UserDTO;

public class TestUserClient implements UserClient {
    public UserDTO findById(String userId, String userRepoRef) {
        return create();
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        return create();
    }

    public UserDTO updateAndFindById(String userId, String userRepoRef) {
        return findById(userId, userRepoRef);
    }

    public UserDTO updateAndFindByUsername(String username, String userRepoRef) {
        return findByUsername(username, userRepoRef);
    }

    public String convertAlias(String alias, String userRepoRef) {
        if (alias == null) {
            throw new IllegalStateException(alias);
        }

        return alias.trim().toLowerCase();
    }

    public List<UserDTO> search(String query) {
        return Collections.singletonList(this.create());
    }

    public UserDTO create() {
        UserDTO userDto = new UserDTO();
        userDto.setId("lingo");
        userDto.setUsername("lingo");
        userDto.setDisplayName("lingo");
        userDto.setStatus(1);

        return userDto;
    }
}
