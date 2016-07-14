package com.mossle.api.userauth;

public class MockUserAuthCache implements UserAuthCache {
    public UserAuthDTO findByUsername(String username, String tenantId) {
        return null;
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        return null;
    }

    public UserAuthDTO findById(String id, String tenantId) {
        return null;
    }

    public void updateUserAuth(UserAuthDTO userAuthDto) {
    }

    public void removeUserAuth(UserAuthDTO userAuthDto) {
    }
}
