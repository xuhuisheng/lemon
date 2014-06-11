package com.mossle.ext.store;

import java.io.InputStream;

public interface StoreConnector {
    StoreDTO save(String model, InputStream inputStream, String originName)
            throws Exception;

    StoreDTO get(String model, String key) throws Exception;
}
