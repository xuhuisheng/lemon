package com.mossle.ext.cache;

import java.util.HashMap;
import java.util.Map;

public class LocalCacheStrategy implements CacheStrategy {
    private Map<String, Cache> cacheMap = new HashMap<String, Cache>();

    public Cache getCache(String name) {
        Cache cache = cacheMap.get(name);

        if (cache != null) {
            return cache;
        }

        cache = new MapCache();
        cacheMap.put(name, cache);

        return cache;
    }
}
