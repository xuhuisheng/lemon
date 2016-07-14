package com.mossle.security.api;

public interface UserRealm {
    UserAuth login(String username, String password, String tenantId);

    UserAuth findByRef(String ref, String tenantId);

    UserAuth findById(String id, String tenantId);
}
