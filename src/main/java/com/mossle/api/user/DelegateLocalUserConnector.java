package com.mossle.api.user;

import javax.annotation.Resource;

public class DelegateLocalUserConnector implements LocalUserConnector {
    private RemoteUserConnector remoteUserConnector;

    public UserDTO findById(String userId, String userRepoRef) {
        return remoteUserConnector.findById(userId, userRepoRef);
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        return remoteUserConnector.findByUsername(username, userRepoRef);
    }

    public UserDTO updateAndFindById(String userId, String userRepoRef) {
        return remoteUserConnector.findById(userId, userRepoRef);
    }

    public UserDTO updateAndFindByUsername(String username, String userRepoRef) {
        return remoteUserConnector.findByUsername(username, userRepoRef);
    }

    public void createOrUpdateLocalUser(UserDTO userDto) {
    }

    @Resource
    public void setRemoteUserConnector(RemoteUserConnector remoteUserConnector) {
        this.remoteUserConnector = remoteUserConnector;
    }
}
