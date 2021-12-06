package com.mossle.internal.open.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.open.persistence.domain.SysEntry;
import com.mossle.internal.open.persistence.domain.SysInfo;
import com.mossle.internal.open.persistence.manager.SysEntryManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysEntryCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(SysEntryCallback.class);
    private SysInfoManager sysInfoManager;
    private SysEntryManager sysEntryManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String sysCode = list.get(0);
        String name = list.get(1);
        String type = list.get(2);
        String platform = list.get(3);
        String url = list.get(4);

        SysInfo sysInfo = sysInfoManager.findUniqueBy("code", sysCode);

        if (sysInfo == null) {
            logger.info("cannot find sys : {}", sysCode);

            return;
        }

        String code = sysCode + "-" + type + "-" + platform;
        SysEntry sysEntry = sysEntryManager.findUniqueBy("code", code);

        if (sysEntry != null) {
            return;
        }

        sysEntry = new SysEntry();
        sysEntry.setCode(code);
        sysEntry.setName(name);
        sysEntry.setPriority(lineNo);
        sysEntry.setType(type);
        sysEntry.setUrl(url);
        sysEntry.setPlatform(platform);
        sysEntry.setSysInfo(sysInfo);
        sysEntry.setStatus("active");
        sysEntryManager.save(sysEntry);
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    public void setSysInfoManager(SysInfoManager sysInfoManager) {
        this.sysInfoManager = sysInfoManager;
    }

    public void setSysEntryManager(SysEntryManager sysEntryManager) {
        this.sysEntryManager = sysEntryManager;
    }
}
