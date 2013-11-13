package com.mossle.security.api;

import java.util.List;
import java.util.Map;

public interface UserInfo {
    String getUsername();

    String getDisplayName();

    String getPassword();

    List<String> getAuthorities();

    List<String> getAttributes();

    Map<String, Object> getExtra();
}
