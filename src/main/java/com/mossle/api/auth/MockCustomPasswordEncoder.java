package com.mossle.api.auth;

public class MockCustomPasswordEncoder implements CustomPasswordEncoder {
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(rawPassword);
    }
}
