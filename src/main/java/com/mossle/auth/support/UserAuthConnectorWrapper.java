package com.mossle.auth.support;

import com.mossle.api.userauth.UserAuthCache;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

public class UserAuthConnectorWrapper implements UserAuthConnector {
    private UserAuthConnector userAuthConnector;
    private UserAuthCache userAuthCache;

    public UserAuthDTO findByUsername(String username, String tenantId) {
        UserAuthDTO userAuthDto = userAuthCache.findByUsername(username,
                tenantId);

        if (userAuthDto == null) {
            synchronized (userAuthCache) {
                userAuthDto = userAuthCache.findByUsername(username, tenantId);

                if (userAuthDto == null) {
                    userAuthDto = userAuthConnector.findByUsername(username,
                            tenantId);

                    if (userAuthDto != null) {
                        userAuthCache.updateUserAuth(userAuthDto);
                    }
                }
            }
        }

        return userAuthDto;
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        UserAuthDTO userAuthDto = userAuthCache.findByRef(ref, tenantId);

        if (userAuthDto == null) {
            synchronized (userAuthCache) {
                userAuthDto = userAuthCache.findByRef(ref, tenantId);

                if (userAuthDto == null) {
                    userAuthDto = userAuthConnector.findByRef(ref, tenantId);

                    if (userAuthDto != null) {
                        userAuthCache.updateUserAuth(userAuthDto);
                    }
                }
            }
        }

        return userAuthDto;
    }

    public UserAuthDTO findById(String id, String tenantId) {
        UserAuthDTO userAuthDto = userAuthCache.findById(id, tenantId);

        if (userAuthDto == null) {
            synchronized (userAuthCache) {
                userAuthDto = userAuthCache.findById(id, tenantId);

                if (userAuthDto == null) {
                    userAuthDto = userAuthConnector.findById(id, tenantId);

                    if (userAuthDto != null) {
                        userAuthCache.updateUserAuth(userAuthDto);
                    }
                }
            }
        }

        return userAuthDto;
    }

    public void setUserAuthConnector(UserAuthConnector userAuthConnector) {
        this.userAuthConnector = userAuthConnector;
    }

    public void setUserAuthCache(UserAuthCache userAuthCache) {
        this.userAuthCache = userAuthCache;
    }
}
