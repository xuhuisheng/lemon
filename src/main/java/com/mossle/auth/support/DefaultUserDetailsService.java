package com.mossle.auth.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mossle.auth.domain.Perm;
import com.mossle.auth.domain.Role;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.manager.UserStatusManager;

import com.mossle.security.impl.UserInfoImpl;
import com.mossle.security.spi.UserStatusFetcher;
import com.mossle.security.spi.UserStatusUpdater;
import com.mossle.security.util.UserDetailsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public class DefaultUserDetailsService implements UserDetailsService,
        MessageSourceAware {
    private Logger logger = LoggerFactory
            .getLogger(DefaultUserDetailsService.class);
    private MessageSourceAccessor messages;
    private UserStatusManager userStatusManager;
    private UserStatusFetcher userStatusFetcher;
    private UserStatusUpdater userStatusUpdater;
    private Set<String> localUsers = new HashSet<String>();

    public DefaultUserDetailsService() {
        localUsers.add("lingo.mossle");
        localUsers.add("vivian.mossle");
    }

    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        if (!localUsers.contains(username)) {
            this.fetchUser(username);
        }

        UserStatus userStatus = userStatusManager.findUniqueBy("username",
                username);

        if (userStatus == null) {
            // 此处需要支持设置找不到用户的默认配置
            // 如果是与单点登录结合，找不到的用户也应该以没有任何权限的方式登录到系统里
            logger.warn("cannot find user : {}", username);
            userStatus = new UserStatus();
            userStatus.setUsername(username);
            userStatus.setPassword("");
            userStatus.setStatus(1);
        }

        Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();

        loadUserAuthorities(userStatus, dbAuthsSet);

        UserInfoImpl userInfo = new UserInfoImpl();
        userInfo.setUsername(username);
        userInfo.setPassword(userStatus.getPassword());
        userInfo.setAuthorities(this.getAuthorities(userStatus));
        userInfo.setAttributes(Collections.EMPTY_LIST);

        UserDetails userDetails = new UserDetailsBuilder(userInfo,
                userStatus.getPassword()).build();

        return userDetails;
    }

    protected void fetchUser(String username) {
        if (userStatusFetcher != null) {
            int status = 0;

            try {
                status = userStatusFetcher.getUserStatus(username);
                userStatusUpdater.updateUser(username, status);
            } catch (UsernameNotFoundException ex) {
                logger.debug("Query returned no results for user '" + username
                        + "'");
                userStatusUpdater.removeUser(username);

                throw new UsernameNotFoundException(messages.getMessage(
                        "JdbcDaoImpl.notFound", new Object[] { username },
                        "Username {0} not found"), ex);
            }
        }
    }

    protected List<String> getAuthorities(UserStatus userStatus) {
        List<String> authorities = new ArrayList<String>();

        for (Role role : userStatus.getRoles()) {
            for (Perm perm : role.getRoleDef().getPerms()) {
                String text = perm.getName();
                authorities.add(text);
            }
        }

        return authorities;
    }

    protected void loadUserAuthorities(UserStatus userStatus,
            Set<GrantedAuthority> authsSet) {
        for (Role role : userStatus.getRoles()) {
            for (Perm perm : role.getRoleDef().getPerms()) {
                String text = perm.getName();
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
                        text);
                authsSet.add(grantedAuthority);
            }
        }
    }

    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setUserStatusFetcher(UserStatusFetcher userStatusFetcher) {
        this.userStatusFetcher = userStatusFetcher;
    }

    public void setUserStatusUpdater(UserStatusUpdater userStatusUpdater) {
        this.userStatusUpdater = userStatusUpdater;
    }

    public void setLocalUsers(Set<String> localUsers) {
        this.localUsers = localUsers;
    }
}
