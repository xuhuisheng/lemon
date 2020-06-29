package com.mossle.internal.open.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.open.persistence.domain.SysCategory;
import com.mossle.internal.open.persistence.domain.SysInfo;
import com.mossle.internal.open.persistence.manager.SysCategoryManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysInfoCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(SysInfoCallback.class);
    private SysInfoManager sysInfoManager;
    private SysCategoryManager sysCategoryManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);
        String category = list.get(2);
        String logo = list.get(3);
        String url = list.get(4);

        SysCategory sysCategory = sysCategoryManager.findUniqueBy("name",
                category);

        if (sysCategory == null) {
            sysCategory = new SysCategory();
            sysCategory.setName(category);
            sysCategory.setPriority(lineNo);
            sysCategoryManager.save(sysCategory);
        }

        SysInfo sysInfo = sysInfoManager.findUniqueBy("code", code);

        if (sysInfo != null) {
            return;
        }

        sysInfo = new SysInfo();
        sysInfo.setCode(code);
        sysInfo.setName(name);
        sysInfo.setPriority(lineNo);
        sysInfo.setLogo(logo);
        sysInfo.setUrl(url);
        sysInfo.setSysCategory(sysCategory);
        sysInfo.setStatus("active");
        sysInfoManager.save(sysInfo);
    }

    public void setSysInfoManager(SysInfoManager sysInfoManager) {
        this.sysInfoManager = sysInfoManager;
    }

    public void setSysCategoryManager(SysCategoryManager sysCategoryManager) {
        this.sysCategoryManager = sysCategoryManager;
    }
}
