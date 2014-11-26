package com.mossle.security.client;

import com.mossle.ext.auth.CurrentUserHolder;

import com.mossle.security.util.SpringSecurityUtils;

public class SpringSecurityCurrentUserHolder implements CurrentUserHolder {
    public String getUserId() {
        return SpringSecurityUtils.getCurrentUserId();
    }

    public String getUsername() {
        return SpringSecurityUtils.getCurrentUsername();
    }
}
