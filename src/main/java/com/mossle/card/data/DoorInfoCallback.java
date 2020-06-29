package com.mossle.card.data;

import java.util.List;

import com.mossle.card.persistence.domain.DoorInfo;
import com.mossle.card.persistence.manager.DoorInfoManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoorInfoCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(DoorInfoCallback.class);
    private DoorInfoManager doorInfoManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.trim().toLowerCase();

        this.createOrUpdateDoorInfo(code, name, lineNo);
    }

    public void createOrUpdateDoorInfo(String code, String name, int lineNo) {
        DoorInfo doorInfo = doorInfoManager.findUniqueBy("code", code);

        if (doorInfo != null) {
            return;
        }

        // insert
        doorInfo = new DoorInfo();
        doorInfo.setCode(code);
        doorInfo.setName(name);
        doorInfo.setStatus("active");
        // assetInfo.setTenantId(defaultTenantId);
        doorInfoManager.save(doorInfo);
    }

    // ~
    public void setDoorInfoManager(DoorInfoManager doorInfoManager) {
        this.doorInfoManager = doorInfoManager;
    }
}
