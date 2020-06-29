package com.mossle.internal.oss.web.rs;

import com.mossle.internal.oss.persistence.domain.OssAccess;
import com.mossle.internal.oss.persistence.manager.OssAccessManager;

import com.mossle.spi.rpc.AccessSecretHelper;

public class AccessSecretHelperImpl implements AccessSecretHelper {
    private OssAccessManager storeAppManager;

    public AccessSecretHelperImpl(OssAccessManager storeAppManager) {
        this.storeAppManager = storeAppManager;
    }

    public String findAccessSecret(String accessKey) {
        OssAccess ossAccess = storeAppManager.findUniqueBy("accessKey",
                accessKey);

        if (ossAccess == null) {
            return null;
        }

        return ossAccess.getSecretKey();
    }
}
