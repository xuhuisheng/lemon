package com.mossle.security.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mossle.api.userauth.UserAuthDTO;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SpringSecurityUserAuth extends UserAuthDTO implements UserDetails {
    private Collection<? extends GrantedAuthority> authorities;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void getAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public boolean isEnabled() {
        return "1".equals(this.getStatus());
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

    // ~ ==================================================
    public void setPermissions(List<String> permissions) {
        super.setPermissions(permissions);

        if (authorities != null) {
            return;
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }

        this.authorities = authorities;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(getId());
        out.writeObject(getScopeId());
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        setId((String) in.readObject());
        setScopeId((String) in.readObject());
    }
}
