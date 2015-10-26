package com.mossle.core.cache;

import java.util.HashMap;
import java.util.Map;

public class RemoteCacheStrategy implements CacheStrategy {
    private Cache cache = new MapCache();

    public Cache getCache(String name) {
        return cache;
    }
}
