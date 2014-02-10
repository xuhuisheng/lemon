package com.mossle.security.api;

public interface UserRequest {
    String getId();

    String getScopeId();

    String getUsername();

    String getPassword();
}
