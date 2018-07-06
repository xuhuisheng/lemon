package com.mossle.security.client;

public interface AuthenticationStrategy {
    boolean authenticate(String username, String password);
}
