package com.mossle.security.client;

import java.util.Collections;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.security.impl.SpringSecurityUserAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultUserDetailsService implements UserDetailsService {
    private static Logger logger = LoggerFactory
            .getLogger(DefaultUserDetailsService.class);
    private UserAuthConnector userAuthConnector;
    private String defaultPassword;
    private BeanMapper beanMapper = new BeanMapper();
    private boolean debug;

    /**
     * 遇到的问题.
     * 
     * 主要流程为 1.判断用户是否存在 2.读取用户权限 3.创建UserDetails
     */
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        logger.debug("username : {}", username);

        if (debug) {
            SpringSecurityUserAuth userAuth = new SpringSecurityUserAuth();
            userAuth.setId("0");
            userAuth.setUsername(username);
            userAuth.setDisplayName(username);
            userAuth.setPermissions(Collections.singletonList("*"));

            return userAuth;
        }

        try {
            UserAuthDTO userAuthDto = userAuthConnector.findByUsername(
                    username, ScopeHolder.getScopeId());

            SpringSecurityUserAuth userAuthResult = new SpringSecurityUserAuth();
            beanMapper.copy(userAuthDto, userAuthResult);

            if (defaultPassword != null) {
                userAuthResult.setPassword(defaultPassword);
            }

            return userAuthResult;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new UsernameNotFoundException(username, ex);
        }
    }

    public void setUserAuthConnector(UserAuthConnector userAuthConnector) {
        this.userAuthConnector = userAuthConnector;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
