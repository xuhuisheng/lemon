package com.mossle.security.client;

import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.security.impl.SpringSecurityUserAuth;
import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class CachedSecurityContextRepository extends
        HttpSessionSecurityContextRepository {
    private UserAuthConnector userAuthConnector;
    private BeanMapper beanMapper = new BeanMapper();
    private boolean debug;

    public SecurityContext loadContext(
            HttpRequestResponseHolder requestResponseHolder) {
        SecurityContext securityContext = super
                .loadContext(requestResponseHolder);

        if (securityContext == null) {
            logger.debug("securityContext is null");

            return null;
        }

        if (debug) {
            return securityContext;
        }

        SpringSecurityUserAuth userAuthInSession = SpringSecurityUtils
                .getCurrentUser(securityContext);

        if (userAuthInSession == null) {
            logger.debug("userAuthInSession is null");

            return securityContext;
        }

        UserAuthDTO userAuthInCache = userAuthConnector.findById(
                userAuthInSession.getId(), userAuthInSession.getTenantId());

        SpringSecurityUserAuth userAuthResult = new SpringSecurityUserAuth();
        beanMapper.copy(userAuthInCache, userAuthResult);

        SpringSecurityUtils.saveUserDetailsToContext(userAuthResult, null,
                securityContext);

        return securityContext;
    }

    public void setUserAuthConnector(UserAuthConnector userAuthConnector) {
        this.userAuthConnector = userAuthConnector;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
