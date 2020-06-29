package com.mossle.api.store;

import java.io.InputStream;

import java.util.UUID;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.client.oss.OssClient;

import com.mossle.core.store.InputStreamDataSource;

public class OssStoreConnector implements StoreConnector {
    private OssClient ossClient;

    public StoreDTO saveStore(String model, DataSource dataSource,
            String tenantId) throws Exception {
        String key = UUID.randomUUID().toString();

        return this.saveStore(model, key, dataSource, tenantId);
    }

    public StoreDTO saveStore(String model, String key, DataSource dataSource,
            String tenantId) throws Exception {
        String budgetName = model;
        String objectName = key;
        String result = this.ossClient.putObject(budgetName, objectName,
                dataSource.getInputStream());

        if (result == null) {
            return null;
        }

        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(budgetName);
        storeDto.setKey(objectName);
        storeDto.setDataSource(dataSource);
        storeDto.setDisplayName(objectName);

        return storeDto;
    }

    public StoreDTO getStore(String model, String key, String tenantId)
            throws Exception {
        String budgetName = model;
        String objectName = key;
        InputStream inputStream = ossClient.getObject(budgetName, objectName);
        StoreDTO storeDto = new StoreDTO();

        storeDto.setModel(budgetName);
        storeDto.setKey(objectName);
        storeDto.setDataSource(new InputStreamDataSource(objectName,
                inputStream));
        storeDto.setDisplayName(objectName);

        return storeDto;
    }

    public void removeStore(String model, String key, String tenantId)
            throws Exception {
        String budgetName = model;
        String objectName = key;
        this.ossClient.deleteObject(budgetName, objectName);
    }

    @Resource
    public void setOssClient(OssClient ossClient) {
        this.ossClient = ossClient;
    }
}
