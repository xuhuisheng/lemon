package com.mossle.security.client;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.web.filter.GenericFilterBean;

public class AutoLoginFilter extends GenericFilterBean {
    private UserDetailsService userDetailsService;
    private boolean enabled = false;
    private String defaultUserName;

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (enabled && (SpringSecurityUtils.getCurrentUser() == null)) {
            UserDetails userDetails = userDetailsService
                    .loadUserByUsername(defaultUserName);

            if (userDetails == null) {
                throw new UsernameNotFoundException(defaultUserName);
            }

            SpringSecurityUtils.saveUserDetailsToContext(userDetails,
                    (HttpServletRequest) request);
        }

        chain.doFilter(request, response);
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultUserName() {
        return defaultUserName;
    }

    public void setDefaultUserName(String defaultUserName) {
        this.defaultUserName = defaultUserName;
    }
}
