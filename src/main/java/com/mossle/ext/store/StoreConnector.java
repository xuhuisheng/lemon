package com.mossle.ext.store;

import java.io.InputStream;

import org.springframework.core.io.Resource;

public interface StoreConnector {
    StoreDTO save(String model, Resource resource, String originName)
            throws Exception;

    StoreDTO get(String model, String key) throws Exception;

    void remove(String model, String key) throws Exception;
}
