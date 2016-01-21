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
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public boolean isCredentialsNonExpired() {
        return !this.isCredentialsExpired();
    }

    public boolean isAccountNonLocked() {
        return !this.isAccountLocked();
    }

    public boolean isAccountNonExpired() {
        return !this.isAccountExpired();
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
        out.writeObject(getTenantId());
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        setId((String) in.readObject());
        setTenantId((String) in.readObject());
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof UserAuthDTO) {
            return this.getUsername().equals(((UserAuthDTO) rhs).getUsername());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getUsername().hashCode();
    }
}
