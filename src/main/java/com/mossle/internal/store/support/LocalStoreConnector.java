package com.mossle.internal.store.support;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;

import com.mossle.ext.store.StoreHelper;
import com.mossle.ext.store.StoreResult;

public class LocalStoreConnector implements StoreConnector {
    private StoreHelper storeHelper;

    public StoreDTO saveStore(String model, DataSource dataSource)
            throws Exception {
        StoreResult storeResult = storeHelper.saveStore(model, dataSource);

        return this.convertStoreDto(storeResult);
    }

    public StoreDTO saveStore(String model, String key, DataSource dataSource)
            throws Exception {
        StoreResult storeResult = storeHelper.saveStore(model, key, dataSource);

        return this.convertStoreDto(storeResult);
    }

    public StoreDTO getStore(String model, String key) throws Exception {
        StoreResult storeResult = storeHelper.getStore(model, key);

        if (storeResult == null) {
            return null;
        }

        return this.convertStoreDto(storeResult);
    }

    public void removeStore(String model, String key) throws Exception {
        storeHelper.removeStore(model, key);
    }

    public StoreDTO convertStoreDto(StoreResult storeResult) {
        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(storeResult.getModel());
        storeDto.setKey(storeResult.getKey());
        storeDto.setDataSource(storeResult.getDataSource());
        storeDto.setDisplayName(storeResult.getKey());

        return storeDto;
    }

    @Resource
    public void setStoreHelper(StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
    }
}
