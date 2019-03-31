package com.mossle.internal.store.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.internal.store.persistence.manager.StoreAppManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreDeployer {
    private static Logger logger = LoggerFactory.getLogger(StoreDeployer.class);
    private StoreAppManager storeAppManager;
    private String dataFilePath = "data/store-app.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;
    private StoreAppCallback storeAppCallback;

    public void init() {
        storeAppCallback = new StoreAppCallback();
        storeAppCallback.setStoreAppManager(storeAppManager);
    }

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init {}", StoreDeployer.class);

            return;
        }

        this.init();

        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                storeAppCallback);
    }

    @Resource
    public void setStoreAppManager(StoreAppManager storeAppManager) {
        this.storeAppManager = storeAppManager;
    }
}
