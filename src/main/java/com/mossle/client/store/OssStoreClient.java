package com.mossle.client.store;

import java.io.InputStream;

import java.util.UUID;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.store.StoreDTO;

import com.mossle.client.oss.OssClient;

import com.mossle.core.store.InputStreamDataSource;

public class OssStoreClient implements StoreClient {
    private OssClient ossClient;

    // get object
    public StoreDTO getStore(String budgetName, String objectName,
            String tenantId) throws Exception {
        InputStream inputStream = ossClient.getObject(budgetName, objectName);
        StoreDTO storeDto = new StoreDTO();

        storeDto.setModel(budgetName);
        storeDto.setKey(objectName);
        storeDto.setDataSource(new InputStreamDataSource(objectName,
                inputStream));
        storeDto.setDisplayName(objectName);

        return storeDto;
    }

    // post object and generate object name
    public StoreDTO saveStore(String budgetName, DataSource dataSource,
            String tenantId) throws Exception {
        String objectName = UUID.randomUUID().toString();

        return this.saveStore(budgetName, objectName, dataSource, tenantId);
    }

    // put object and use object name
    public StoreDTO saveStore(String budgetName, String objectName,
            DataSource dataSource, String tenantId) throws Exception {
        this.ossClient.putObject(budgetName, objectName,
                dataSource.getInputStream());

        StoreDTO storeDto = new StoreDTO();
        storeDto.setModel(budgetName);
        storeDto.setKey(objectName);
        storeDto.setDataSource(dataSource);
        storeDto.setDisplayName(objectName);

        return storeDto;
    }

    // delete object
    public void removeStore(String budgetName, String objectName,
            String tenantId) throws Exception {
        this.ossClient.deleteObject(budgetName, objectName);
    }

    @Resource
    public void setOssClient(OssClient ossClient) {
        this.ossClient = ossClient;
    }
}
