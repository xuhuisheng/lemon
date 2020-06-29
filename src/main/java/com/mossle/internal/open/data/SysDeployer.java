package com.mossle.internal.open.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.internal.open.persistence.manager.SysCategoryManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysDeployer {
    private static Logger logger = LoggerFactory.getLogger(SysDeployer.class);
    private SysCategoryManager sysCategoryManager;
    private SysInfoManager sysInfoManager;
    private String dataFilePath = "data/sys.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;
    private SysInfoCallback sysInfoCallback;

    public void init() {
        sysInfoCallback = new SysInfoCallback();
        sysInfoCallback.setSysCategoryManager(sysCategoryManager);
        sysInfoCallback.setSysInfoManager(sysInfoManager);
    }

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init {}", SysDeployer.class);

            return;
        }

        this.init();

        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                sysInfoCallback);
    }

    @Resource
    public void setSysCategoryManager(SysCategoryManager sysCategoryManager) {
        this.sysCategoryManager = sysCategoryManager;
    }

    @Resource
    public void setSysInfoManager(SysInfoManager sysInfoManager) {
        this.sysInfoManager = sysInfoManager;
    }
}
