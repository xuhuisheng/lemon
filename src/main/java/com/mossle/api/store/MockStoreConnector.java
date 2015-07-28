package com.mossle.api.store;

import javax.activation.DataSource;

public class MockStoreConnector implements StoreConnector {
    public StoreDTO saveStore(String model, DataSource dataSource)
            throws Exception {
        return null;
    }

    public StoreDTO saveStore(String model, String key, DataSource dataSource)
            throws Exception {
        return null;
    }

    public StoreDTO getStore(String model, String key) throws Exception {
        return null;
    }

    public void removeStore(String model, String key) throws Exception {
    }
}
