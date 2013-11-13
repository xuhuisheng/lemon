package com.mossle.security.region;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

public class RegionPermissionVoter implements AccessDecisionVoter<Object> {
    private static Logger logger = LoggerFactory
            .getLogger(RegionPermissionVoter.class);
    private PermissionChecker permissionChecker;

    public boolean supports(ConfigAttribute attribute) {
        return attribute.getAttribute() != null;
    }

    public boolean supports(Class<?> clazz) {
        return true;
    }

    public int vote(Authentication authentication, Object object,
            Collection<ConfigAttribute> configAttributes) {
        int result = ACCESS_ABSTAIN;

        for (ConfigAttribute configAttribute : configAttributes) {
            if (this.supports(configAttribute)) {
                result = ACCESS_DENIED;

                String text = getPermission(object, configAttribute);
                logger.debug("text : {}", text);

                boolean authorized = permissionChecker.isAuthorized(text);

                if (authorized) {
                    return ACCESS_GRANTED;
                }
            }
        }

        return result;
    }

    private String getPermission(Object object, ConfigAttribute configAttribute) {
        String permission = configAttribute.getAttribute();

        if (!(object instanceof FilterInvocation)) {
            return permission;
        }

        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        String region = request.getParameter("region");

        if (region == null) {
            region = RegionConstants.SYSTEM_REGION;
        }

        StringBuffer buff = new StringBuffer();
        String[] array = permission.split(",");

        for (String text : array) {
            buff.append(region).append(":").append(text).append(",");
        }

        buff.deleteCharAt(buff.length() - 1);
        permission = buff.toString();

        return permission;
    }

    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }
}
