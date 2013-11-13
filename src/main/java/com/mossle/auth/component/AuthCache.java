package com.mossle.auth.component;

import javax.annotation.Resource;

import com.mossle.auth.domain.Perm;
import com.mossle.auth.domain.Role;
import com.mossle.auth.domain.RoleDef;
import com.mossle.auth.domain.UserStatus;

import org.springframework.security.core.userdetails.UserCache;

import org.springframework.stereotype.Component;

@Component
public class AuthCache {
    private UserCache userCache;

    public void evictUserStatus(UserStatus userStatus) {
        userCache.removeUserFromCache(userStatus.getUsername());
    }

    public void evictRole(Role role) {
        for (UserStatus userStatus : role.getUserStatuses()) {
            evictUserStatus(userStatus);
        }
    }

    public void evictPerm(Perm perm) {
        for (RoleDef roleDef : perm.getRoleDefs()) {
            for (Role role : roleDef.getRoles()) {
                evictRole(role);
            }
        }
    }

    @Resource
    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }
}
