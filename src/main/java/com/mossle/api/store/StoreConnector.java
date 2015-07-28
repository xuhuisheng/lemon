package com.mossle.api.store;

import javax.activation.DataSource;

public interface StoreConnector {
    StoreDTO saveStore(String model, DataSource dataSource) throws Exception;

    StoreDTO saveStore(String model, String key, DataSource dataSource)
            throws Exception;

    StoreDTO getStore(String model, String key) throws Exception;

    void removeStore(String model, String key) throws Exception;
}
