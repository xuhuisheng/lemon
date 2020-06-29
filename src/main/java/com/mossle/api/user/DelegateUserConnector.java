package com.mossle.api.user;

import java.util.Map;

import javax.annotation.Resource;

import com.mossle.client.user.UserClient;

import com.mossle.core.page.Page;

public class DelegateUserConnector implements UserConnector {
    private UserClient userClient;

    public UserDTO findById(String id) {
        String tenantId = "1";

        return this.userClient.findById(id, tenantId);
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        return this.userClient.findByUsername(username, userRepoRef);
    }

    public UserDTO findByRef(String ref, String userRepoRef) {
        return null;
    }

    public Page pagedQuery(String userRepoRef, Page page,
            Map<String, Object> parameters) {
        return null;
    }

    public UserDTO findByNickName(String nickName, String userRepoRef) {
        return null;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
