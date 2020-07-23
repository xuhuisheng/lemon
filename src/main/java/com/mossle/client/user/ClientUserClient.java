package com.mossle.client.user;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.user.LocalUserConnector;
import com.mossle.api.user.RemoteUserConnector;
import com.mossle.api.user.UserDTO;

/**
 * client模式.
 * 
 * <pre>
 * 先从本地查询user，
 * 如果找不到，就去remote查询user，同步至local。
 * 如果local查询到user，直接使用本地的user。
 * </pre>
 */
public class ClientUserClient implements UserClient {
    private LocalUserConnector localUserConnector;
    private RemoteUserConnector remoteUserConnector;

    public UserDTO findById(String userId, String userRepoRef) {
        UserDTO userDto = localUserConnector.findById(userId, userRepoRef);

        if (userDto != null) {
            return userDto;
        }

        return updateAndFindById(userId, userRepoRef);
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        UserDTO userDto = localUserConnector.findByUsername(username,
                userRepoRef);

        if (userDto != null) {
            return userDto;
        }

        return updateAndFindByUsername(username, userRepoRef);
    }

    public UserDTO updateAndFindById(String userId, String userRepoRef) {
        UserDTO userDto = remoteUserConnector.findById(userId, userRepoRef);

        if (userDto == null) {
            throw new IllegalStateException("cannot find user remote : "
                    + userId);
        }

        this.localUserConnector.createOrUpdateLocalUser(userDto);

        return userDto;
    }

    public UserDTO updateAndFindByUsername(String username, String userRepoRef) {
        UserDTO userDto = remoteUserConnector.findByUsername(username,
                userRepoRef);

        if (userDto == null) {
            throw new IllegalStateException("cannot find user remote : "
                    + username);
        }

        this.localUserConnector.createOrUpdateLocalUser(userDto);

        return userDto;
    }

    public String convertAlias(String alias, String userRepoRef) {
        if (alias == null) {
            throw new IllegalStateException(alias);
        }

        return alias.trim().toLowerCase();
    }

    public List<UserDTO> search(String query) {
        return this.remoteUserConnector.search(query);
    }

    // ~
    @Resource
    public void setLocalUserConnector(LocalUserConnector localUserConnector) {
        this.localUserConnector = localUserConnector;
    }

    @Resource
    public void setRemoteUserConnector(RemoteUserConnector remoteUserConnector) {
        this.remoteUserConnector = remoteUserConnector;
    }
}
