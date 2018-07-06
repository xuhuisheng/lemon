package com.mossle.security.client;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.RemoteUserConnector;

import com.mossle.spi.user.InternalUserConnector;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class SimpleAuthenticationProvider extends DaoAuthenticationProvider {
    private RemoteUserConnector remoteUserConnector;

    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String username = userDetails.getUsername();
        String presentedPassword = authentication.getCredentials().toString();
        boolean isValid = this.remoteUserConnector.authenticate(username,
                presentedPassword);

        if (!isValid) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"), userDetails);
        }
    }

    @Resource
    public void setRemoteUserConnector(RemoteUserConnector remoteUserConnector) {
        this.remoteUserConnector = remoteUserConnector;
    }
}
