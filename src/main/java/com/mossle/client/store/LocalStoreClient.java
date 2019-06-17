package com.mossle.client.store;

import javax.activation.DataSource;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.store.StoreDTO;

import com.mossle.core.store.FileStoreHelper;
import com.mossle.core.store.StoreResult;

import org.springframework.beans.factory.annotation.Value;

public class LocalStoreClient implements StoreClient {
    private FileStoreHelper storeHelper;
    private String baseDir;

    @PostConstruct
    public void init() {
        storeHelper = new FileStoreHelper();
        storeHelper.setBaseDir(baseDir);
    }

    public StoreDTO getStore(String budgetName, String objectName,
            String tenantId) throws Exception {
        StoreResult storeResult = storeHelper.getStore(tenantId + "/"
                + budgetName, objectName);

        if (storeResult == null) {
            return null;
        }

        return this.convertStoreDto(storeResult);
    }

    public StoreDTO saveStore(String budgetName, DataSource dataSource,
            String tenantId) throws Exception {
        StoreResult storeResult = storeHelper.saveStore(tenantId + "/"
                + budgetName, dataSource);

        return this.convertStoreDto(storeResult);
    }

    public StoreDTO saveStore(String budgetName, String objectName,
            DataSource dataSource, String tenantId) throws Exception {
        StoreResult storeResult = storeHelper.saveStore(tenantId + "/"
                + budgetName, objectName, dataSource);

        return this.convertStoreDto(storeResult);
    }

    public void removeStore(String budgetName, String objectName,
            String tenantId) throws Exception {
        storeHelper.removeStore(tenantId + "/" + budgetName, objectName);
    }

    @Value("${store.baseDir}")
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    // ~
    public StoreDTO convertStoreDto(StoreResult storeResult) {
        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(storeResult.getModel());
        storeDto.setKey(storeResult.getKey());
        storeDto.setDataSource(storeResult.getDataSource());
        storeDto.setDisplayName(storeResult.getKey());

        return storeDto;
    }
}
