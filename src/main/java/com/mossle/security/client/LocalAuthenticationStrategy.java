package com.mossle.security.client;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.spi.user.InternalUserConnector;

public class LocalAuthenticationStrategy implements AuthenticationStrategy {
    private CustomPasswordEncoder customPasswordEncoder;
    private InternalUserConnector internalUserConnector;
    private TenantHolder tenantHolder;

    public boolean authenticate(String username, String password) {
        String tenantId = tenantHolder.getTenantId();
        String passwordInDatabase = internalUserConnector.findPassword(
                username, tenantId);
        boolean isValid = customPasswordEncoder.matches(password,
                passwordInDatabase);

        return isValid;
    }

    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }

    public void setInternalUserConnector(
            InternalUserConnector internalUserConnector) {
        this.internalUserConnector = internalUserConnector;
    }

    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
