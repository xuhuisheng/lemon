package com.mossle.api.user;

public interface UserCache {
    UserDTO findById(String id);

    UserDTO findByUsername(String username, String userRepoRef);

    UserDTO findByRef(String ref, String userRepoRef);

    void updateUser(UserDTO userDto);

    void removeUser(UserDTO userDto);
}
