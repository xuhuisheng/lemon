package com.mossle.internal.open.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.internal.open.persistence.manager.OpenAppManager;
import com.mossle.internal.open.persistence.manager.SysCategoryManager;
import com.mossle.internal.open.persistence.manager.SysEntryManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.internal.open.data.SysDeployer")
public class SysDeployer {
    private static Logger logger = LoggerFactory.getLogger(SysDeployer.class);
    private SysCategoryManager sysCategoryManager;
    private SysInfoManager sysInfoManager;
    private SysEntryManager sysEntryManager;
    private OpenAppManager openAppManager;
    private String dataFilePath = "data/open/sys.csv";
    private String dataFileEncoding = "GB2312";
    private String entryDataFilePath = "data/open/sys-entry.csv";
    private String defaultTenantId = "1";
    private boolean enable = true;
    private SysInfoCallback sysInfoCallback;
    private SysEntryCallback sysEntryCallback;

    public void init() {
        // info
        sysInfoCallback = new SysInfoCallback();
        sysInfoCallback.setSysCategoryManager(sysCategoryManager);
        sysInfoCallback.setSysInfoManager(sysInfoManager);
        sysInfoCallback.setOpenAppManager(openAppManager);
        sysInfoCallback.setDefaultTenantId(defaultTenantId);
        // entry
        sysEntryCallback = new SysEntryCallback();
        sysEntryCallback.setSysInfoManager(sysInfoManager);
        sysEntryCallback.setSysEntryManager(sysEntryManager);
        sysEntryCallback.setDefaultTenantId(defaultTenantId);
    }

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip sys init data");

            return;
        }

        logger.info("start sys init data");

        this.init();

        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                sysInfoCallback);
        new CsvProcessor().process(entryDataFilePath, dataFileEncoding,
                sysEntryCallback);
        logger.info("end sys init data");
    }

    @Resource
    public void setSysCategoryManager(SysCategoryManager sysCategoryManager) {
        this.sysCategoryManager = sysCategoryManager;
    }

    @Resource
    public void setSysInfoManager(SysInfoManager sysInfoManager) {
        this.sysInfoManager = sysInfoManager;
    }

    @Resource
    public void setSysEntryManager(SysEntryManager sysEntryManager) {
        this.sysEntryManager = sysEntryManager;
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
