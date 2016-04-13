package com.mossle.user.support;

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
    private Cache<String, byte[]> cache;

    @PostConstruct
    public void init() {
        this.cache = cacheManager.getCache("avatar");
    }

    public DataSource getByCode(String code) {
        byte[] bytes = this.cache.get(code);

        if (bytes == null) {
            return null;
        }

        return new ByteArrayDataSource(bytes);
    }

    public void updateDataSource(String code, DataSource dataSource) {
        try {
            byte[] bytes = IOUtils.toByteArray(dataSource.getInputStream());
            this.cache.put(code, bytes);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Resource
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
