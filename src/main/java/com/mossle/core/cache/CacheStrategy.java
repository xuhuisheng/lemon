package com.mossle.core.cache;

public interface CacheStrategy {
    Cache getCache(String name);
}
