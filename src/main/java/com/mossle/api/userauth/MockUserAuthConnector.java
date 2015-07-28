package com.mossle.api.userauth;

public class MockUserAuthConnector implements UserAuthConnector {
    public UserAuthDTO findByUsername(String username, String scopeId) {
        return null;
    }

    public UserAuthDTO findByRef(String ref, String scopeId) {
        return null;
    }

    public UserAuthDTO findById(String id, String scopeId) {
        return null;
    }
}
