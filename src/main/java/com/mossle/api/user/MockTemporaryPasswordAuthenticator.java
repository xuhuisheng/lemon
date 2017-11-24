package com.mossle.api.user;

public class MockTemporaryPasswordAuthenticator implements
        TemporaryPasswordAuthenticator {
    public String authenticate(String userId, String password) {
        return AccountStatus.SUCCESS;
    }
}
