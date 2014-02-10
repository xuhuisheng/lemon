package com.mossle.security.impl;

import com.mossle.security.api.UserAuth;
import com.mossle.security.api.UserRealm;

public class UserRealmWrapper implements UserRealm {
    public UserAuth login(String username, String password, String scopeId) {
        return null;
    }

    public UserAuth findByRef(String ref, String scopeId) {
        return null;
    }

    public UserAuth findById(String id, String scopeId) {
        return null;
    }
}
