package com.mossle.internal.store.web.rs;

import com.mossle.internal.store.persistence.domain.StoreApp;
import com.mossle.internal.store.persistence.manager.StoreAppManager;

import com.mossle.spi.rpc.AccessSecretHelper;

public class AccessSecretHelperImpl implements AccessSecretHelper {
    private StoreAppManager storeAppManager;

    public AccessSecretHelperImpl(StoreAppManager storeAppManager) {
        this.storeAppManager = storeAppManager;
    }

    public String findAccessSecret(String accessKey) {
        StoreApp storeApp = storeAppManager.findUniqueBy("appId", accessKey);

        if (storeApp == null) {
            return null;
        }

        return storeApp.getAppKey();
    }
}
