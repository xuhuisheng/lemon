package com.mossle.simulator.jcache;

import java.net.URI;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;

public class DefaultCacheManager implements CacheManager {
    private CachingProvider cachingProvider;
    private Map<String, Cache> cacheMap = new HashMap<String, Cache>();

    public DefaultCacheManager(CachingProvider cachingProvider) {
        this.cachingProvider = cachingProvider;
    }

    public CachingProvider getCachingProvider() {
        return cachingProvider;
    }

    public URI getURI() {
        return null;
    }

    public ClassLoader getClassLoader() {
        return null;
    }

    public Properties getProperties() {
        return null;
    }

    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(
            String cacheName, C configuration) throws IllegalArgumentException {
        Cache cache = new DefaultCache(cacheName, this);
        cacheMap.put(cacheName, cache);

        return cache;
    }

    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType,
            Class<V> valueType) {
        Cache<K, V> cache = (Cache<K, V>) cacheMap.get(cacheName);

        if (cache != null) {
            return cache;
        }

        return this.createCache(cacheName, (Configuration<K, V>) null);
    }

    public <K, V> Cache<K, V> getCache(String cacheName) {
        return this.getCache(cacheName, null, null);
    }

    public Iterable<String> getCacheNames() {
        return cacheMap.keySet();
    }

    public void destroyCache(String cacheName) {
    }

    public void enableManagement(String cacheName, boolean enabled) {
    }

    public void enableStatistics(String cacheName, boolean enabled) {
    }

    public void close() {
    }

    public boolean isClosed() {
        return false;
    }

    public <T> T unwrap(Class<T> clazz) {
        return null;
    }
}
