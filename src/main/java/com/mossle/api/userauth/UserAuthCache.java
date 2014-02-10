package com.mossle.api.userauth;

public interface UserAuthCache {
    UserAuthDTO findByUsername(String username, String scopeId);

    UserAuthDTO findByRef(String ref, String scopeId);

    UserAuthDTO findById(String id, String scopeId);

    void updateUserAuth(UserAuthDTO userAuthDto);

    void removeUserAuth(UserAuthDTO userAuthDto);
}
