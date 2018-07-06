package com.mossle.api.auth;

public class MockPermissionHelper implements PermissionHelper {
    public boolean hasPermission(String... permssions) {
        return true;
    }

    public boolean lackPermission(String... permissions) {
        return false;
    }

    public boolean hasRole(String... roles) {
        return true;
    }

    public boolean lackRole(String... roles) {
        return false;
    }
}
