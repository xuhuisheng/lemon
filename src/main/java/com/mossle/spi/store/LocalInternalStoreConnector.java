package com.mossle.spi.store;

import javax.activation.DataSource;

import com.mossle.api.store.StoreDTO;

import com.mossle.core.store.StoreHelper;
import com.mossle.core.store.StoreResult;

public class LocalInternalStoreConnector implements InternalStoreConnector {
    private StoreHelper storeHelper;

    public StoreDTO saveStore(String model, DataSource dataSource,
            String tenantId) throws Exception {
        StoreResult storeResult = storeHelper.saveStore(tenantId + "/" + model,
                dataSource);

        return this.convertStoreDto(storeResult);
    }

    public StoreDTO saveStore(String model, String key, DataSource dataSource,
            String tenantId) throws Exception {
        StoreResult storeResult = storeHelper.saveStore(tenantId + "/" + model,
                key, dataSource);

        return this.convertStoreDto(storeResult);
    }

    public StoreDTO getStore(String model, String key, String tenantId)
            throws Exception {
        StoreResult storeResult = storeHelper.getStore(tenantId + "/" + model,
                key);

        if (storeResult == null) {
            return null;
        }

        return this.convertStoreDto(storeResult);
    }

    public void removeStore(String model, String key, String tenantId)
            throws Exception {
        storeHelper.removeStore(tenantId + "/" + model, key);
    }

    public StoreDTO convertStoreDto(StoreResult storeResult) {
        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(storeResult.getModel());
        storeDto.setKey(storeResult.getKey());
        storeDto.setDataSource(storeResult.getDataSource());
        storeDto.setDisplayName(storeResult.getKey());

        return storeDto;
    }

    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }
}
