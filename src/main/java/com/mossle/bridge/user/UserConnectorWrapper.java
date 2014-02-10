package com.mossle.bridge.user;

import java.util.List;
import java.util.Map;

import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.page.Page;

public class UserConnectorWrapper implements UserConnector {
    private UserConnector userConnector;
    private UserCache userCache;

    public UserDTO findById(String id) {
        UserDTO userDto = userCache.findById(id);

        if (userDto == null) {
            synchronized (userCache) {
                userDto = userCache.findById(id);

                if (userDto == null) {
                    userDto = userConnector.findById(id);

                    if (userDto != null) {
                        userCache.updateUser(userDto);
                    }
                }
            }
        }

        return userDto;
    }

    public UserDTO findByUsername(String username, String userRepoRef) {
        UserDTO userDto = userCache.findByUsername(username, userRepoRef);

        if (userDto == null) {
            synchronized (userCache) {
                userDto = userCache.findByUsername(username, userRepoRef);

                if (userDto == null) {
                    userDto = userConnector.findByUsername(username,
                            userRepoRef);

                    if (userDto != null) {
                        userCache.updateUser(userDto);
                    }
                }
            }
        }

        return userDto;
    }

    public UserDTO findByRef(String ref, String userRepoRef) {
        UserDTO userDto = userCache.findByRef(ref, userRepoRef);

        if (userDto == null) {
            synchronized (userCache) {
                userDto = userCache.findByRef(ref, userRepoRef);

                if (userDto == null) {
                    userDto = userConnector.findByRef(ref, userRepoRef);

                    if (userDto != null) {
                        userCache.updateUser(userDto);
                    }
                }
            }
        }

        return userDto;
    }

    public Page pagedQuery(Page page, Map<String, Object> parameters) {
        return userConnector.pagedQuery(page, parameters);
    }

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }
}
