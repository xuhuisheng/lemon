package com.mossle.security.api;

public interface UserRealm {
    UserAuth login(String username, String password, String scopeId);

    UserAuth findByRef(String ref, String scopeId);

    UserAuth findById(String id, String scopeId);
}
