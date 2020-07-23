package com.mossle.api.user;

import java.util.List;

public interface LocalUserConnector {
    UserDTO findById(String userId, String userRepoRef);

    UserDTO findByUsername(String username, String userRepoRef);

    UserDTO updateAndFindById(String userId, String userRepoRef);

    UserDTO updateAndFindByUsername(String username, String userRepoRef);

    void createOrUpdateLocalUser(UserDTO userDto);

    List<UserDTO> search(String query);
}
