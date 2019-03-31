package com.mossle.internal.sendmail.web.rs;

import com.mossle.internal.sendmail.persistence.domain.SendmailApp;
import com.mossle.internal.sendmail.persistence.manager.SendmailAppManager;

import com.mossle.spi.rpc.AccessSecretHelper;

public class AccessSecretHelperImpl implements AccessSecretHelper {
    private SendmailAppManager sendmailAppManager;

    public AccessSecretHelperImpl(SendmailAppManager sendmailAppManager) {
        this.sendmailAppManager = sendmailAppManager;
    }

    public String findAccessSecret(String accessKey) {
        SendmailApp sendmailApp = sendmailAppManager.findUniqueBy("appId",
                accessKey);

        if (sendmailApp == null) {
            return null;
        }

        return sendmailApp.getAppKey();
    }
}
