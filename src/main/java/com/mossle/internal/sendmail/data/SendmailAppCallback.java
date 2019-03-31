package com.mossle.internal.sendmail.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.sendmail.persistence.domain.SendmailApp;
import com.mossle.internal.sendmail.persistence.manager.SendmailAppManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendmailAppCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(SendmailAppCallback.class);
    private SendmailAppManager sendmailAppManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String groupName = list.get(1);
        String configCode = list.get(2);
        String priority = list.get(3);
        String appId = list.get(4);
        String appKey = list.get(5);

        SendmailApp sendmailApp = sendmailAppManager.findUniqueBy("name", name);

        if (sendmailApp != null) {
            return;
        }

        sendmailApp = new SendmailApp();
        sendmailApp.setName(name);
        sendmailApp.setGroupName(groupName);
        sendmailApp.setConfigCode(configCode);
        sendmailApp.setPriority(Integer.parseInt(priority));
        sendmailApp.setAppId(appId);
        sendmailApp.setAppKey(appKey);
        // sendmailApp.setTenantId(defaultTenantId);
        sendmailAppManager.save(sendmailApp);
    }

    public void setSendmailAppManager(SendmailAppManager sendmailAppManager) {
        this.sendmailAppManager = sendmailAppManager;
    }
}
