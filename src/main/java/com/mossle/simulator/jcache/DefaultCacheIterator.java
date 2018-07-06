package com.mossle.simulator.jcache;

import java.util.Iterator;
import java.util.Map;

import javax.cache.Cache;

public class DefaultCacheIterator<K, V> implements Iterator<Cache.Entry<K, V>> {
    private Iterator<Map.Entry<K, V>> iterator;

    public DefaultCacheIterator(Map<K, V> data) {
        this.iterator = data.entrySet().iterator();
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Cache.Entry<K, V> next() {
        Map.Entry<K, V> entry = this.iterator.next();

        return new DefaultCacheEntry(entry);
    }

    public void remove() {
        this.iterator.remove();
    }
}
