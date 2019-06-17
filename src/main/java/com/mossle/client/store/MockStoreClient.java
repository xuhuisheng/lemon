package com.mossle.client.store;

import javax.activation.DataSource;

import com.mossle.api.store.StoreDTO;

public class MockStoreClient implements StoreClient {
    public StoreDTO getStore(String budgetName, String objectName,
            String tenantId) throws Exception {
        return null;
    }

    public StoreDTO saveStore(String budgetName, DataSource dataSource,
            String tenantId) throws Exception {
        return null;
    }

    public StoreDTO saveStore(String budgetName, String objectName,
            DataSource dataSource, String tenantId) throws Exception {
        return null;
    }

    public void removeStore(String budgetName, String objectName,
            String tenantId) throws Exception {
    }
}
