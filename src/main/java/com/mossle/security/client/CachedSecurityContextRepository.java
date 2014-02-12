package com.mossle.security.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        if (debug) {
            return securityContext;
        }

        if (securityContext != null) {
            SpringSecurityUserAuth userAuthInSession = SpringSecurityUtils
                    .getCurrentUser(securityContext);

            if (userAuthInSession != null) {
                UserAuthDTO userAuthInCache = userAuthConnector.findById(
                        userAuthInSession.getId(),
                        userAuthInSession.getScopeId());
                SpringSecurityUserAuth userAuthResult = new SpringSecurityUserAuth();
                beanMapper.copy(userAuthInCache, userAuthResult);
                SpringSecurityUtils.saveUserDetailsToContext(userAuthResult,
                        null);
            }
        }

        return securityContext;
    }

    public void saveContext(SecurityContext context,
            HttpServletRequest request, HttpServletResponse response) {
        super.saveContext(context, request, response);
    }

    public boolean containsContext(HttpServletRequest request) {
        return super.containsContext(request);
    }

    public void setUserAuthConnector(UserAuthConnector userAuthConnector) {
        this.userAuthConnector = userAuthConnector;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
