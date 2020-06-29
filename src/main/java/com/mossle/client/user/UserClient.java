package com.mossle.client.user;

import java.util.List;

import com.mossle.api.user.UserDTO;

public interface UserClient {
    UserDTO findById(String userId, String userRepoRef);

    UserDTO findByUsername(String username, String userRepoRef);

    UserDTO updateAndFindById(String userId, String userRepoRef);

    UserDTO updateAndFindByUsername(String username, String userRepoRef);

    String convertAlias(String alias, String userRepoRef);

    List<UserDTO> search(String query);
}
