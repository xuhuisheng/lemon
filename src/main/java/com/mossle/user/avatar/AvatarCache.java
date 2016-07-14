package com.mossle.user.avatar;

import java.util.HashSet;
import java.util.Set;

import javax.activation.DataSource;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.cache.Cache;
import javax.cache.CacheManager;

import com.mossle.core.store.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvatarCache {
    private static Logger logger = LoggerFactory.getLogger(AvatarCache.class);
    private CacheManager cacheManager;
    private Cache<String, byte[]> dataCache;
    private Cache<String, Set<String>> aliasCache;

    @PostConstruct
    public void init() {
        this.dataCache = cacheManager.getCache("avatar");
        this.aliasCache = cacheManager.getCache("avatar-alias");
    }

    public DataSource getDataSource(String userId, int width) {
        String key = userId + ":" + width;
        byte[] bytes = this.dataCache.get(key);

        if (bytes == null) {
            return null;
        }

        return new ByteArrayDataSource(bytes);
    }

    public void updateDataSource(String userId, int width, DataSource dataSource) {
        try {
            String key = userId + ":" + width;
            byte[] bytes = IOUtils.toByteArray(dataSource.getInputStream());
            Set<String> aliasValue = this.aliasCache.get(userId);

            if (aliasValue == null) {
                aliasValue = new HashSet<String>();
                this.aliasCache.put(userId, aliasValue);
            }

            aliasValue.add(key);
            this.dataCache.put(key, bytes);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void removeDataSource(String userId) {
        Set<String> aliasValue = this.aliasCache.get(userId);

        if (aliasValue == null) {
            return;
        }

        for (String alias : aliasValue) {
            this.dataCache.remove(alias);
        }
    }

    @Resource
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
