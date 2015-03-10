package com.mossle.api.user;

public interface UserCache {
    UserDTO findById(String id);

    UserDTO findByUsername(String username, String userRepoRef);

    UserDTO findByRef(String ref, String userRepoRef);

    UserDTO findByNickName(String nickName);

    void updateUser(UserDTO userDto);

    void removeUser(UserDTO userDto);
}
