package com.mossle.internal.store.client;

import java.io.InputStream;

import com.mossle.api.store.StoreDTO;

public interface StoreClient {
    StoreDTO saveStore(InputStream inputStream, String fileName,
            String contentType, String tenantId) throws Exception;

    StoreDTO getStore(String key, String tenantId) throws Exception;
}
