package com.mossle.api.userauth;

public interface UserAuthCache {
    UserAuthDTO findByUsername(String username, String tenantId);

    UserAuthDTO findByRef(String ref, String tenantId);

    UserAuthDTO findById(String id, String tenantId);

    void updateUserAuth(UserAuthDTO userAuthDto);

    void removeUserAuth(UserAuthDTO userAuthDto);
}
