package com.mossle.api.userauth;

public class MockUserAuthConnector implements UserAuthConnector {
    public UserAuthDTO findByUsername(String username, String tenantId) {
        return null;
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        return null;
    }

    public UserAuthDTO findById(String id, String tenantId) {
        return null;
    }
}
