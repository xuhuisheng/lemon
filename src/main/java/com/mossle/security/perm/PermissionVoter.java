package com.mossle.security.perm;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

public class PermissionVoter implements AccessDecisionVoter<Object> {
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

                String text = getPermission(configAttribute);
                boolean authorized = permissionChecker.isAuthorized(text);

                if (authorized) {
                    return ACCESS_GRANTED;
                }
            }
        }

        return result;
    }

    private String getPermission(ConfigAttribute configAttribute) {
        return configAttribute.getAttribute();
    }

    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }
}
