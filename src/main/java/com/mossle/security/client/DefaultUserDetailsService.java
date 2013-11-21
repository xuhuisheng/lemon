package com.mossle.security.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mossle.security.api.UserFetcher;
import com.mossle.security.api.UserInfo;
import com.mossle.security.impl.MockUserFetcher;
import com.mossle.security.util.UserDetailsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultUserDetailsService implements UserDetailsService {
    private static Logger logger = LoggerFactory
            .getLogger(DefaultUserDetailsService.class);
    private UserFetcher userFetcher = new MockUserFetcher();
    private String defaultPassword;

    /**
     * 遇到的问题.
     * 
     * 主要流程为 1.判断用户是否存在 2.读取用户权限 3.创建UserDetails
     */
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        logger.debug("username : {}", username);
        logger.debug("userFetcher : {}", userFetcher);

        try {
            UserInfo userInfo = userFetcher.getUserInfo(username);

            String password = userInfo.getPassword();

            if (defaultPassword != null) {
                password = defaultPassword;
            }

            UserDetails userDetails = new UserDetailsBuilder(userInfo, password)
                    .build();

            return userDetails;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new UsernameNotFoundException(username, ex);
        }
    }

    public void setUserFetcher(UserFetcher userFetcher) {
        this.userFetcher = userFetcher;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }
}
