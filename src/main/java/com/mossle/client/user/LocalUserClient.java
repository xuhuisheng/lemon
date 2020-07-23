package com.mossle.client.user;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.user.LocalUserConnector;
import com.mossle.api.user.RemoteUserConnector;
import com.mossle.api.user.UserDTO;

/**
 * local模式.
 * 
 * <pre>
 * 本地查询user，找不到就报错。
 * </pre>
 */
public class LocalUserClient implements UserClient {
    private LocalUserConnector localUserConnector;

    public UserDTO findById(String userId, String userRepoRef) {
        UserDTO userDto = localUserConnector.findById(userId, userRepoRef);

        if (userDto == null) {
            throw new IllegalStateException("cannot find user : " + userId);
        }

        return userDto;
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        UserDTO userDto = localUserConnector.findByUsername(username,
                userRepoRef);

        if (userDto == null) {
            throw new IllegalStateException("cannot find user : " + username);
        }

        return userDto;
    }

    public UserDTO updateAndFindById(String userId, String userRepoRef) {
        return this.findById(userId, userRepoRef);
    }

    public UserDTO updateAndFindByUsername(String username, String userRepoRef) {
        return this.findByUsername(username, userRepoRef);
    }

    public String convertAlias(String alias, String userRepoRef) {
        if (alias == null) {
            throw new IllegalStateException(alias);
        }

        return alias.trim().toLowerCase();
    }

    public List<UserDTO> search(String query) {
        return this.localUserConnector.search(query);
    }

    // ~
    @Resource
    public void setLocalUserConnector(LocalUserConnector localUserConnector) {
        this.localUserConnector = localUserConnector;
    }
}
