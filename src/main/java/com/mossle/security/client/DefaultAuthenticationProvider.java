package com.mossle.security.client;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.RemoteUserConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.client.authn.AuthnClient;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.util.BaseDTO;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class DefaultAuthenticationProvider extends DaoAuthenticationProvider {
    private AuthnClient authnClient;
    private TenantHolder tenantHolder;
    private BeanMapper beanMapper = new BeanMapper();

    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String username = userDetails.getUsername();
        String presentedPassword = authentication.getCredentials().toString();

        String tenantId = tenantHolder.getTenantId();

        String result = authnClient.authenticate(username, presentedPassword,
                tenantId);

        boolean isValid = AccountStatus.SUCCESS.equals(result);

        if (!isValid) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"), userDetails);
        }
    }

    @Resource
    public void setAuthnClient(AuthnClient authnClient) {
        this.authnClient = authnClient;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
