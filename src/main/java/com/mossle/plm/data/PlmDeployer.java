package com.mossle.plm.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.plm.persistence.manager.PlmProductManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.plm.data.PlmDeployer")
public class PlmDeployer {
    private static Logger logger = LoggerFactory.getLogger(PlmDeployer.class);
    private PlmProductManager plmProductManager;
    private String dataFilePath = "data/plm/product.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip plm init data");

            return;
        }

        logger.info("start plm init data");

        PlmProductCallback plmProductCallback = new PlmProductCallback();
        plmProductCallback.setPlmProductManager(plmProductManager);
        plmProductCallback.setDefaultTenantId(defaultTenantId);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                plmProductCallback);
        logger.info("end plm init data");
    }

    @Resource
    public void setPlmProductManager(PlmProductManager plmProductManager) {
        this.plmProductManager = plmProductManager;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    @Value("${plm.data.init.enable:false}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
