package com.mossle.ext.cache;

public interface CacheStrategy {
    Cache getCache(String name);
}
