package com.mossle.api.user;

public class MockUserCache implements UserCache {
    public UserDTO findById(String id) {
        UserDTO userDto = new UserDTO();
        userDto.setId(id);
        userDto.setUsername(id);
        userDto.setDisplayName(id);

        return userDto;
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        UserDTO userDto = new UserDTO();
        userDto.setId(username);
        userDto.setUsername(username);
        userDto.setDisplayName(username);

        return userDto;
    }

    public UserDTO findByRef(String ref, String userRepoRef) {
        UserDTO userDto = new UserDTO();
        userDto.setId(ref);
        userDto.setUsername(ref);
        userDto.setDisplayName(ref);

        return userDto;
    }

    public UserDTO findByNickName(String nickName) {
        UserDTO userDto = new UserDTO();
        userDto.setId(nickName);
        userDto.setUsername(nickName);
        userDto.setDisplayName(nickName);

        return userDto;
    }

    public void updateUser(UserDTO userDto) {
    }

    public void removeUser(UserDTO userDto) {
    }
}
