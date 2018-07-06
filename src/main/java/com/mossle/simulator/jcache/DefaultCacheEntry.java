package com.mossle.simulator.jcache;

import java.util.Map;

import javax.cache.Cache;

public class DefaultCacheEntry<K, V> implements Cache.Entry<K, V> {
    private Map.Entry<K, V> entry;

    public DefaultCacheEntry(Map.Entry<K, V> entry) {
        this.entry = entry;
    }

    public K getKey() {
        return entry.getKey();
    }

    public V getValue() {
        return entry.getValue();
    }

    public <T> T unwrap(Class<T> clazz) {
        return null;
    }
}
