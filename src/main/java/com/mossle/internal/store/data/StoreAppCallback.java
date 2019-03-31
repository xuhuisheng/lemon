package com.mossle.internal.store.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.store.persistence.domain.StoreApp;
import com.mossle.internal.store.persistence.manager.StoreAppManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreAppCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(StoreAppCallback.class);
    private StoreAppManager storeAppManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String groupName = list.get(1);
        String modelCode = list.get(2);
        String priority = list.get(3);
        String appId = list.get(4);
        String appKey = list.get(5);

        StoreApp storeApp = storeAppManager.findUniqueBy("name", name);

        if (storeApp != null) {
            return;
        }

        storeApp = new StoreApp();
        storeApp.setName(name);
        storeApp.setGroupName(groupName);
        storeApp.setModelCode(modelCode);
        storeApp.setPriority(Integer.parseInt(priority));
        storeApp.setAppId(appId);
        storeApp.setAppKey(appKey);
        // sendmailApp.setTenantId(defaultTenantId);
        storeAppManager.save(storeApp);
    }

    public void setStoreAppManager(StoreAppManager storeAppManager) {
        this.storeAppManager = storeAppManager;
    }
}
