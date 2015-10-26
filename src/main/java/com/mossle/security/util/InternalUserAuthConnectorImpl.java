package com.mossle.security.util;

import javax.servlet.http.HttpSession;

import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.spi.userauth.InternalUserAuthConnector;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public class InternalUserAuthConnectorImpl implements InternalUserAuthConnector {
    public UserAuthDTO findFromSession(HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session
                .getAttribute("SPRING_SECURITY_CONTEXT");

        if (securityContext == null) {
            return null;
        }

        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserAuthDTO)) {
            return null;
        }

        return ((UserAuthDTO) principal);
    }
}
