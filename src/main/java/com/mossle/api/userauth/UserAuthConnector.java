package com.mossle.api.userauth;

public interface UserAuthConnector {
    UserAuthDTO findByUsername(String username, String scopeId);

    UserAuthDTO findByRef(String ref, String scopeId);

    UserAuthDTO findById(String id, String scopeId);
}
