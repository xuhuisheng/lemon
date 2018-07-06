package com.mossle.api.auth;

public class MockCurrentUserHolder implements CurrentUserHolder {
    public String getUserId() {
        return "1";
    }

    public String getUsername() {
        return "lingo";
    }
}
