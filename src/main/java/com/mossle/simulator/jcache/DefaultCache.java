package com.mossle.simulator.jcache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

public class DefaultCache<K, V> implements Cache<K, V> {
    private String name;
    private CacheManager cacheManager;
    private Map<K, V> data = new HashMap<K, V>();

    public DefaultCache(String name, CacheManager cacheManager) {
        this.name = name;
        this.cacheManager = cacheManager;
    }

    public V get(K key) {
        return data.get(key);
    }

    public Map<K, V> getAll(Set<? extends K> keys) {
        Map<K, V> map = new HashMap<K, V>();

        for (K key : keys) {
            map.put(key, data.get(key));
        }

        return map;
    }

    public boolean containsKey(K key) {
        return data.containsKey(key);
    }

    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues,
            CompletionListener completionListener) {
    }

    public void put(K key, V value) {
        data.put(key, value);
    }

    public V getAndPut(K key, V value) {
        V oldValue = data.get(key);
        data.put(key, value);

        return oldValue;
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        data.putAll(map);
    }

    public boolean putIfAbsent(K key, V value) {
        if (this.containsKey(key)) {
            data.put(key, value);
        }

        return false;
    }

    public boolean remove(K key) {
        if (data.containsKey(key)) {
            data.remove(key);

            return true;
        }

        return false;
    }

    public boolean remove(K key, V oldValue) {
        if (!data.containsKey(key)) {
            return false;
        }

        V value = data.get(key);

        if (value == null) {
            return false;
        }

        if (value != oldValue) {
            return false;
        }

        data.remove(key);

        return true;
    }

    public V getAndRemove(K key) {
        return data.remove(key);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (this.remove(key, oldValue)) {
            data.put(key, newValue);

            return true;
        }

        return false;
    }

    public boolean replace(K key, V value) {
        if (this.remove(key)) {
            data.put(key, value);

            return true;
        }

        return false;
    }

    public V getAndReplace(K key, V value) {
        V oldValue = this.getAndRemove(key);

        if (oldValue == null) {
            return null;
        }

        data.put(key, value);

        return oldValue;
    }

    public void removeAll(Set<? extends K> keys) {
        for (K key : keys) {
            data.remove(key);
        }
    }

    public void removeAll() {
        data.clear();
    }

    public void clear() {
        data.clear();
    }

    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        return null;
    }

    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor,
            Object... arguments) throws EntryProcessorException {
        return null;
    }

    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys,
            EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        return null;
    }

    public String getName() {
        return name;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void close() {
    }

    public boolean isClosed() {
        return false;
    }

    public <T> T unwrap(Class<T> clazz) {
        return null;
    }

    public void registerCacheEntryListener(
            CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
    }

    public void deregisterCacheEntryListener(
            CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
    }

    public Iterator<Cache.Entry<K, V>> iterator() {
        return null;
    }
}
