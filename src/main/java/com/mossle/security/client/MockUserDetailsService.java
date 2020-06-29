package com.mossle.security.client;

import java.util.Collections;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.client.authz.AuthzClient;
import com.mossle.client.user.UserClient;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.security.impl.SpringSecurityUserAuth;

import com.mossle.spi.user.InternalUserConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MockUserDetailsService implements UserDetailsService {
    private static Logger logger = LoggerFactory
            .getLogger(MockUserDetailsService.class);
    private UserClient userClient;
    private AuthzClient authzClient;
    private TenantHolder tenantHolder;
    private BeanMapper beanMapper = new BeanMapper();

    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        logger.debug("username : {}", username);

        String tenantId = tenantHolder.getTenantId();
        String processedUsername = userClient.convertAlias(username, tenantId);

        SpringSecurityUserAuth userAuth = new SpringSecurityUserAuth();
        UserAuthDTO userAuthDto = authzClient.findByUsername(processedUsername,
                tenantId);

        if (userAuthDto == null) {
            logger.info("cannot find user auth : {} {}", processedUsername,
                    tenantId);

            return null;
        }

        beanMapper.copy(userAuthDto, userAuth);

        return userAuth;
    }

    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    public void setAuthzClient(AuthzClient authzClient) {
        this.authzClient = authzClient;
    }

    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
