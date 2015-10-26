package com.mossle.core.store;

import java.io.InputStream;

import javax.activation.DataSource;

import org.springframework.core.io.Resource;

public interface StoreHelper {
    StoreResult getStore(String model, String key) throws Exception;

    void removeStore(String model, String key) throws Exception;

    StoreResult saveStore(String model, DataSource dataSource) throws Exception;

    StoreResult saveStore(String model, String key, DataSource dataSource)
            throws Exception;
}
