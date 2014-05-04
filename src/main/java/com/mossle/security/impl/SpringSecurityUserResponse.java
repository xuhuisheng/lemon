package com.mossle.security.impl;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SpringSecurityUserResponse extends UserResponseImpl implements
        UserDetails {
    private Collection<? extends GrantedAuthority> authorities;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void getAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getPassword() {
        return "NO_PASSWORD";
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isAccountNonExpired() {
        return true;
    }
}
