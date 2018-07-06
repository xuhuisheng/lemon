package com.mossle.security.support;

import com.mossle.api.auth.PermissionHelper;

import com.mossle.security.util.SpringSecurityUtils;

public class SpringSecurityPermissionHelper implements PermissionHelper {
    public boolean hasPermission(String... permissions) {
        return SpringSecurityUtils.hasPermission(permissions);
    }

    public boolean lackPermission(String... permissions) {
        return SpringSecurityUtils.lackPermission(permissions);
    }

    public boolean hasRole(String... permissions) {
        return SpringSecurityUtils.hasRole(permissions);
    }

    public boolean lackRole(String... permissions) {
        return SpringSecurityUtils.lackRole(permissions);
    }
}
