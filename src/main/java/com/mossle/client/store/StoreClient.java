package com.mossle.client.store;

import javax.activation.DataSource;

import com.mossle.api.store.StoreDTO;

public interface StoreClient {
    // get object
    StoreDTO getStore(String budgetName, String objectName, String tenantId)
            throws Exception;

    // post object and generate object name
    StoreDTO saveStore(String budgetName, DataSource dataSource, String tenantId)
            throws Exception;

    // put object and use object name
    StoreDTO saveStore(String budgetName, String objectName,
            DataSource dataSource, String tenantId) throws Exception;

    // delete object
    void removeStore(String budgetName, String objectName, String tenantId)
            throws Exception;
}
