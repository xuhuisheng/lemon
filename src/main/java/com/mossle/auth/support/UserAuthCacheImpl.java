package com.mossle.auth.support;

import com.mossle.api.userauth.UserAuthCache;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.core.cache.Cache;
import com.mossle.core.cache.CacheStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAuthCacheImpl implements UserAuthCache {
    private static Logger logger = LoggerFactory
            .getLogger(UserAuthCacheImpl.class);
    private CacheStrategy cacheStrategy;
    private Cache cache;

    public UserAuthDTO findByUsername(String username, String tenantId) {
        if (true) {
            // TODO: skip cache
            return null;
        }

        String key = "userAuthUsername:" + username + ":" + tenantId;

        return cache.get(key);
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        if (true) {
            // TODO: skip cache
            return null;
        }

        String key = "userAuthRef:" + ref + ":" + tenantId;

        return cache.get(key);
    }

    public UserAuthDTO findById(String id, String tenantId) {
        if (true) {
            // TODO: skip cache
            return null;
        }

        String key = "userAuthId:" + id + ":" + tenantId;

        return cache.get(key);
    }

    public void updateUserAuth(UserAuthDTO userAuthDto) {
        logger.debug("update userAuthUsername:{}:{}",
                userAuthDto.getUsername(), userAuthDto.getTenantId());
        logger.debug("update userAuthRef:{}:{}", userAuthDto.getRef(),
                userAuthDto.getTenantId());
        logger.debug("update userAuthId:{}:{}", userAuthDto.getId(),
                userAuthDto.getTenantId());
        cache.set("userAuthUsername:" + userAuthDto.getUsername() + ":"
                + userAuthDto.getTenantId(), userAuthDto);
        cache.set(
                "userAuthRef:" + userAuthDto.getRef() + ":"
                        + userAuthDto.getTenantId(), userAuthDto);
        cache.set(
                "userAuthId:" + userAuthDto.getId() + ":"
                        + userAuthDto.getTenantId(), userAuthDto);
    }

    public void removeUserAuth(UserAuthDTO userAuthDto) {
        if (userAuthDto == null) {
            logger.info("userAuthDto is null");

            return;
        }

        logger.debug("remove userAuthUsername:{}:{}",
                userAuthDto.getUsername(), userAuthDto.getTenantId());
        logger.debug("remove userAuthRef:{}:{}", userAuthDto.getRef(),
                userAuthDto.getTenantId());
        logger.debug("remove userAuthId:{}:{}", userAuthDto.getId(),
                userAuthDto.getTenantId());
        cache.remove("userAuthUsername:" + userAuthDto.getUsername() + ":"
                + userAuthDto.getTenantId());
        cache.remove("userAuthRef:" + userAuthDto.getRef() + ":"
                + userAuthDto.getTenantId());
        cache.remove("userAuthId:" + userAuthDto.getId() + ":"
                + userAuthDto.getTenantId());
    }

    public void setCacheStrategy(CacheStrategy cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
        this.cache = cacheStrategy.getCache("userauth");
    }
}
