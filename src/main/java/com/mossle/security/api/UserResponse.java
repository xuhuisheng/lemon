package com.mossle.security.api;

import java.util.List;

public interface UserResponse {
    String getId();

    String getTenantId();

    String getUsername();

    String getDisplayName();

    String getStatus();

    List<String> getPermissions();

    List<String> getRoles();
}
