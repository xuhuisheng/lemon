package com.mossle.auth.component;

import javax.annotation.Resource;

import com.mossle.auth.persistence.domain.Role;

import com.mossle.core.hibernate.EntityEvent;

import org.springframework.context.ApplicationListener;

import org.springframework.stereotype.Component;

@Component
public class RoleEventListener implements ApplicationListener<EntityEvent> {
    private AuthCache authCache;

    public void onApplicationEvent(EntityEvent event) {
        if (!event.supportsEntityType(Role.class)) {
            return;
        }

        Role role = event.getEntity();
        authCache.evictRole(role);
    }

    @Resource
    public void setAuthCache(AuthCache authCache) {
        this.authCache = authCache;
    }
}
