package com.mossle.security.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

public class AuthenticatedVoter implements AccessDecisionVoter<Object> {
    private static Logger logger = LoggerFactory
            .getLogger(AuthenticatedVoter.class);
    public static final String IS_GUEST = "IS_GUEST";
    public static final String IS_USER = "IS_USER";
    public static final String IS_LOGINED = "IS_LOGINED";
    public static final String IS_SWITCHED = "IS_SWITCHED";
    public static final String IS_REMEMBERED = "IS_REMEMBERED";
    public static final Collection<String> ALLOWED_ATTRIBUTES;

    static {
        List<String> list = new ArrayList<String>();
        list.add(IS_GUEST);
        list.add(IS_USER);
        list.add(IS_LOGINED);
        list.add(IS_SWITCHED);
        list.add(IS_REMEMBERED);
        ALLOWED_ATTRIBUTES = Collections.unmodifiableCollection(list);
    }

    public boolean supports(ConfigAttribute attribute) {
        return (attribute.getAttribute() != null)
                && ALLOWED_ATTRIBUTES.contains(attribute.getAttribute());
    }

    public boolean supports(Class<?> clazz) {
        return true;
    }

    public int vote(Authentication authentication, Object object,
            Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                result = ACCESS_DENIED;

                if (isGuest(authentication, attribute.getAttribute())) {
                    logger.trace("isGuest");

                    return ACCESS_GRANTED;
                }

                if (isUser(authentication, attribute.getAttribute())) {
                    logger.trace("isUser");

                    return ACCESS_GRANTED;
                }

                if (isLogined(authentication, attribute.getAttribute())) {
                    logger.trace("isLogined");

                    return ACCESS_GRANTED;
                }

                if (isSwitched(authentication, attribute.getAttribute())) {
                    logger.trace("isSwitched");

                    return ACCESS_GRANTED;
                }

                if (isRemembered(authentication, attribute.getAttribute())) {
                    logger.trace("isRemembered");

                    return ACCESS_GRANTED;
                }
            }
        }

        logger.trace("attributes : {}", attributes);

        return result;
    }

    // ~ ======================================================================
    public boolean isGuest(Authentication authentication, String attribute) {
        return IS_GUEST.equals(attribute);
    }

    public boolean isUser(Authentication authentication, String attribute) {
        if (!IS_USER.equals(attribute)) {
            return false;
        }

        boolean notGuest = !isOnlyGuest(authentication, IS_GUEST);
        boolean notRemembered = !isRemembered(authentication, IS_REMEMBERED);

        return notGuest && notRemembered;
    }

    public boolean isLogined(Authentication authentication, String attribute) {
        if (!IS_LOGINED.equals(attribute)) {
            return false;
        }

        boolean notGuest = !isOnlyGuest(authentication, IS_GUEST);

        return notGuest;
    }

    // ~ ======================================================================
    public boolean isSwitched(Authentication authentication, String attribute) {
        if (!IS_SWITCHED.equals(attribute)) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication
                .getAuthorities();

        for (GrantedAuthority auth : authorities) {
            if (auth instanceof SwitchUserGrantedAuthority) {
                return true;
            }
        }

        return false;
    }

    public boolean isRemembered(Authentication authentication, String attribute) {
        return IS_REMEMBERED.equals(attribute)
                && RememberMeAuthenticationToken.class
                        .isAssignableFrom(authentication.getClass());
    }

    // ~ ======================================================================
    public boolean isOnlyGuest(Authentication authentication, String attribute) {
        return IS_GUEST.equals(attribute)
                && AnonymousAuthenticationToken.class
                        .isAssignableFrom(authentication.getClass());
    }
}
