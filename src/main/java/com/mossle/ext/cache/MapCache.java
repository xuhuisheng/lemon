package com.mossle.ext.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapCache implements Cache {
    private Map<String, Object> map = new ConcurrentHashMap<String, Object>();

    public <T> T get(String key) {
        return (T) map.get(key);
    }

    public void set(String key, Object value) {
        map.put(key, value);
    }

    public void remove(String key) {
        map.remove(key);
    }
}
