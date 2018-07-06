package com.mossle.security.support;

import com.mossle.api.auth.CustomPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

public class SpringSecurityCustomPasswordEncoder implements
        CustomPasswordEncoder {
    private PasswordEncoder passwordEncoder;

    public SpringSecurityCustomPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
