package com.mossle.security.client;

import java.util.Collections;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.security.impl.SpringSecurityUserAuth;

import com.mossle.spi.user.InternalUserConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultUserDetailsService implements UserDetailsService {
    private static Logger logger = LoggerFactory
            .getLogger(DefaultUserDetailsService.class);
    private UserAuthConnector userAuthConnector;
    private InternalUserConnector internalUserConnector;
    private String defaultPassword;
    private BeanMapper beanMapper = new BeanMapper();
    private boolean debug;
    private TenantHolder tenantHolder;

    /**
     * 遇到的问题.
     * 
     * 主要流程为 1.判断用户是否存在 2.读取用户权限 3.创建UserDetails
     */
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        logger.debug("username : {}", username);

        String tenantId = tenantHolder.getTenantId();

        if (debug) {
            logger.info("debug");

            SpringSecurityUserAuth userAuth = new SpringSecurityUserAuth();
            userAuth.setId("1");
            userAuth.setUsername(username);
            userAuth.setDisplayName(username);
            userAuth.setPermissions(Collections.singletonList("*"));
            userAuth.setTenantId(tenantId);

            return userAuth;
        }

        if (username == null) {
            logger.info("username is null");

            return null;
        }

        username = username.toLowerCase();

        try {
            username = internalUserConnector.findUsernameByAlias(username);

            UserAuthDTO userAuthDto = userAuthConnector.findByUsername(
                    username, tenantId);

            if (userAuthDto == null) {
                logger.info("cannot find user : {}, {}", username, tenantId);

                throw new UsernameNotFoundException(username + "," + tenantId);
            }

            String password = internalUserConnector.findPassword(username,
                    tenantId);

            SpringSecurityUserAuth userAuthResult = new SpringSecurityUserAuth();
            beanMapper.copy(userAuthDto, userAuthResult);
            userAuthResult.setPassword(password);

            if (defaultPassword != null) {
                userAuthResult.setPassword(defaultPassword);
            }

            return userAuthResult;
        } catch (UsernameNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new UsernameNotFoundException(username, ex);
        }
    }

    public void setUserAuthConnector(UserAuthConnector userAuthConnector) {
        this.userAuthConnector = userAuthConnector;
    }

    public void setInternalUserConnector(
            InternalUserConnector internalUserConnector) {
        this.internalUserConnector = internalUserConnector;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
