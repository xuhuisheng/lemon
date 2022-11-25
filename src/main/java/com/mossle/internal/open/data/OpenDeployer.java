package com.mossle.internal.open.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.internal.open.persistence.manager.OpenAppManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.internal.open.data.OpenDeployer")
public class OpenDeployer {
    private static Logger logger = LoggerFactory.getLogger(OpenDeployer.class);
    private OpenAppManager openAppManager;
    private String dataFilePath = "data/open/app.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip open init data");

            return;
        }

        logger.info("start open init data");

        OpenAppCallback openAppCallback = new OpenAppCallback();
        openAppCallback.setDefaultTenantId(defaultTenantId);
        openAppCallback.setOpenAppManager(openAppManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                openAppCallback);
        logger.info("end open init data");
    }

    @Resource
    public void setOpenAppManager(OpenAppManager openAppManager) {
        this.openAppManager = openAppManager;
    }

    @Value("${open.data.init.enable:false}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
