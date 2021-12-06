package com.mossle.internal.open.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.internal.open.persistence.manager.OpenAppManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenDeployer {
    private static Logger logger = LoggerFactory.getLogger(OpenDeployer.class);
    private OpenAppManager openAppManager;
    private String dataFilePath = "data/open-app.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init data");

            return;
        }

        OpenAppCallback openAppCallback = new OpenAppCallback();
        openAppCallback.setDefaultTenantId(defaultTenantId);
        openAppCallback.setOpenAppManager(openAppManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                openAppCallback);
    }

    @Resource
    public void setOpenAppManager(OpenAppManager openAppManager) {
        this.openAppManager = openAppManager;
    }
}
