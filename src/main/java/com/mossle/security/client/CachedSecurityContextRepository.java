package com.mossle.security.client;

import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.client.authz.AuthzClient;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.security.impl.SpringSecurityUserAuth;
import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class CachedSecurityContextRepository extends
        HttpSessionSecurityContextRepository {
    private AuthzClient authzClient;
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

        try {
            SpringSecurityUserAuth userAuthInSession = SpringSecurityUtils
                    .getCurrentUser(securityContext);

            if (userAuthInSession == null) {
                logger.debug("userAuthInSession is null");

                return securityContext;
            }

            UserAuthDTO userAuthInCache = authzClient.findById(
                    userAuthInSession.getId(), userAuthInSession.getTenantId());

            SpringSecurityUserAuth userAuthResult = new SpringSecurityUserAuth();
            beanMapper.copy(userAuthInCache, userAuthResult);

            SpringSecurityUtils.saveUserDetailsToContext(userAuthResult, null,
                    securityContext);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return securityContext;
    }

    public void setAuthzClient(AuthzClient authzClient) {
        this.authzClient = authzClient;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
