package com.mossle.api.user;

public class MockAuthenticationHandler implements AuthenticationHandler {
    public boolean support(String type) {
        return false;
    }

    public String doAuthenticate(String username, String password,
            String application) {
        return AccountStatus.ENABLED;
    }
}
