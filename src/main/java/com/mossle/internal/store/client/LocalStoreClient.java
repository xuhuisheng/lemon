package com.mossle.internal.store.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;

import com.mossle.core.store.ByteArrayDataSource;

import com.mossle.internal.store.service.StoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class LocalStoreClient implements StoreClient {
    private Logger logger = LoggerFactory.getLogger(LocalStoreClient.class);
    private String baseUrl;
    private String model;
    private StoreConnector storeConnector;
    private StoreService storeService;

    public StoreDTO saveStore(InputStream inputStream, String fileName,
            String contentType, String tenantId) throws Exception {
        int len = -1;
        byte[] b = new byte[1024];
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

        while ((len = inputStream.read(b, 0, 1024)) != -1) {
            baos2.write(b, 0, len);
        }

        inputStream.close();

        byte[] bytes = baos2.toByteArray();
        DataSource dataSource = new ByteArrayDataSource(fileName, bytes);
        StoreDTO storeDto = storeService.saveStore(model, dataSource, tenantId);

        return storeDto;
    }

    public StoreDTO getStore(String key, String tenantId) throws Exception {
        return storeConnector.getStore(model, key, tenantId);
    }

    @Value("${store.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Value("${store.model}")
    public void setModel(String model) {
        this.model = model;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }
}
