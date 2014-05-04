package com.mossle.security.util;

import org.springframework.security.crypto.password.PasswordEncoder;

public class Md5PasswordEncoder implements PasswordEncoder {
    private org.springframework.security.authentication.encoding.Md5PasswordEncoder md5PasswordEncoder = new org.springframework.security.authentication.encoding.Md5PasswordEncoder();
    private CharSequence salt;

    public Md5PasswordEncoder() {
    }

    public Md5PasswordEncoder(CharSequence salt) {
        this.salt = salt;
    }

    public String encode(CharSequence rawPassword) {
        return md5PasswordEncoder.encodePassword(rawPassword.toString(), salt);
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return md5PasswordEncoder.isPasswordValid(encodedPassword,
                rawPassword.toString(), salt);
    }
}
