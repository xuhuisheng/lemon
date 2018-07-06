package com.mossle.security.cas;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;
import com.mossle.api.user.UserSyncConnector;

import org.jasig.cas.client.validation.Assertion;

import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class CasAuthenticationUserDetailsService extends
        AbstractCasAssertionUserDetailsService {
    private UserSyncConnector userSyncConnector;
    private UserDetailsService userDetailsService;

    protected UserDetails loadUserDetails(Assertion assertion) {
        String username = assertion.getPrincipal().getName();
        this.updateUserInfo(assertion);

        return userDetailsService.loadUserByUsername(username);
    }

    public void updateUserInfo(Assertion assertion) {
        String username = assertion.getPrincipal().getName();
        String nickName = (String) assertion.getPrincipal().getAttributes()
                .get("nickName");

        if (nickName == null) {
            nickName = username;
        }

        UserDTO userDto = new UserDTO();
        userDto.setRef(username);
        userDto.setUsername(username);
        userDto.setDisplayName(nickName);
        userDto.setNickName(nickName);
        userSyncConnector.updateUser(userDto);
    }

    @Resource
    public void setUserSyncConnector(UserSyncConnector userSyncConnector) {
        this.userSyncConnector = userSyncConnector;
    }

    @Resource
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
