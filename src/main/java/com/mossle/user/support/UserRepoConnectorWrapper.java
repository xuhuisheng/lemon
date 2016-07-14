package com.mossle.user.support;

import java.util.List;

import com.mossle.api.userrepo.UserRepoCache;
import com.mossle.api.userrepo.UserRepoConnector;
import com.mossle.api.userrepo.UserRepoDTO;

public class UserRepoConnectorWrapper implements UserRepoConnector {
    private UserRepoConnector userRepoConnector;
    private UserRepoCache userRepoCache;

    public UserRepoDTO findById(String id) {
        UserRepoDTO userRepoDto = userRepoCache.findById(id);

        if (userRepoDto == null) {
            synchronized (userRepoCache) {
                userRepoDto = userRepoCache.findById(id);

                if (userRepoDto == null) {
                    userRepoDto = userRepoConnector.findById(id);

                    if (userRepoDto != null) {
                        userRepoCache.updateUserRepo(userRepoDto);
                    }
                }
            }
        }

        return userRepoDto;
    }

    public UserRepoDTO findByCode(String code) {
        UserRepoDTO userRepoDto = userRepoCache.findByCode(code);

        if (userRepoDto == null) {
            synchronized (userRepoCache) {
                userRepoDto = userRepoCache.findByCode(code);

                if (userRepoDto == null) {
                    userRepoDto = userRepoConnector.findByCode(code);

                    if (userRepoDto != null) {
                        userRepoCache.updateUserRepo(userRepoDto);
                    }
                }
            }
        }

        return userRepoDto;
    }

    public List<UserRepoDTO> findAll() {
        return userRepoConnector.findAll();
    }

    public void setUserRepoConnector(UserRepoConnector userRepoConnector) {
        this.userRepoConnector = userRepoConnector;
    }

    public void setUserRepoCache(UserRepoCache userRepoCache) {
        this.userRepoCache = userRepoCache;
    }
}
