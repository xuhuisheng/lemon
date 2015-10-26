package com.mossle.security.api;

import java.util.List;
import java.util.Map;

public interface UserInfo {
    String getId();

    String getUsername();

    String getDisplayName();

    String getPassword();

    String getTenantId();

    List<String> getAuthorities();

    List<String> getAttributes();

    Map<String, Object> getExtra();
}
