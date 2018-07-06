package com.mossle.api.auth;

public interface PermissionHelper {
    boolean hasPermission(String... permssions);

    boolean lackPermission(String... permissions);

    boolean hasRole(String... roles);

    boolean lackRole(String... roles);
}
