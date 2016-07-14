package com.mossle.auth.component;

import javax.annotation.Resource;

import com.mossle.auth.persistence.domain.Perm;

import com.mossle.core.hibernate.EntityEvent;

import org.springframework.context.ApplicationListener;

import org.springframework.stereotype.Component;

@Component
public class PermEventListener implements ApplicationListener<EntityEvent> {
    private AuthCache authCache;

    public void onApplicationEvent(EntityEvent event) {
        if (!event.supportsEntityType(Perm.class)) {
            return;
        }

        Perm perm = event.getEntity();
        authCache.evictPerm(perm);
    }

    @Resource
    public void setAuthCache(AuthCache authCache) {
        this.authCache = authCache;
    }
}
