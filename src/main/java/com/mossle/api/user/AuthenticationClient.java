package com.mossle.api.user;

public interface AuthenticationClient {
    String doAuthenticate(String username, String password, String type,
            String application);
}
