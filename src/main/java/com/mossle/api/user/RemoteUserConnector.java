package com.mossle.api.user;

import java.util.List;

public interface RemoteUserConnector {
    UserDTO findById(String userId, String userRepoRef);

    UserDTO findByUsername(String username, String userRepoRef);

    boolean authenticate(String username, String password);

    List<UserDTO> search(String query);
}
