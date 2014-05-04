package com.mossle.security.api;

import java.util.List;

public interface UserAuth {
    String getId();

    String getScopeId();

    String getUsername();

    String getDisplayName();

    String getStatus();

    List<String> getPermissions();

    List<String> getRoles();
}
