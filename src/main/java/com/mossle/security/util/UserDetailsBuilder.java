package com.mossle.security.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mossle.security.api.UserInfo;
import com.mossle.security.impl.UserStatusDetailsImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsBuilder {
    private static Logger logger = LoggerFactory
            .getLogger(UserDetailsBuilder.class);
    private UserInfo userInfo;
    private String password;

    public UserDetailsBuilder(UserInfo userInfo, String password) {
        this.userInfo = userInfo;
        this.password = password;
    }

    public UserDetails build() {
        String username = userInfo.getUsername();

        if (username == null) {
            throw new IllegalArgumentException("username cannot be null");
        }

        if (password == null) {
            throw new IllegalArgumentException("password cannot be null");
        }

        String displayName = userInfo.getDisplayName();

        if (displayName == null) {
            displayName = username;
        }

        // TODO: 以后要强化这里的账号状态判断
        boolean enabled = true;
        Collection<GrantedAuthority> authSet = this
                .loadUserAuthorities(userInfo.getAuthorities());

        List<String> attributes = userInfo.getAttributes();
        UserStatusDetailsImpl userStatusDetailsImpl = new UserStatusDetailsImpl(
                username, password, enabled, authSet);
        userStatusDetailsImpl.setAttributes(attributes);
        userStatusDetailsImpl.setId(userInfo.getId());
        userStatusDetailsImpl.setDisplayName(displayName);
        userStatusDetailsImpl.setTenantId(userInfo.getTenantId());

        return userStatusDetailsImpl;
    }

    protected Collection<GrantedAuthority> loadUserAuthorities(List<String> list) {
        if ((list == null) || list.isEmpty()) {
            logger.debug("no authorities");

            return Collections.EMPTY_LIST;
        }

        Set<GrantedAuthority> authsSet = new HashSet<GrantedAuthority>();

        for (String str : list) {
            authsSet.add(new SimpleGrantedAuthority(str));
        }

        return authsSet;
    }
}
