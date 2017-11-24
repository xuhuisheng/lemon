package com.mossle.api.user;

public interface TemporaryPasswordAuthenticator {
    String authenticate(String userId, String password);
}
