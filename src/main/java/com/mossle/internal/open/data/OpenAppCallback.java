package com.mossle.internal.open.data;

import java.util.Date;
import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.open.persistence.domain.OpenApp;
import com.mossle.internal.open.persistence.manager.OpenAppManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAppCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(OpenAppCallback.class);
    private OpenAppManager openAppManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String groupCode = list.get(0);
        String code = list.get(1);
        String name = list.get(2);
        String clientId = list.get(3);
        String clientSecret = list.get(4);
        String userId = list.get(5);

        if (StringUtils.isBlank(clientId)) {
            logger.warn("clientId cannot be blank {} {}", lineNo, list);

            return;
        }

        if (StringUtils.isBlank(clientSecret)) {
            logger.warn("clientSecret cannot be blank {} {}", lineNo, list);

            return;
        }

        OpenApp openApp = openAppManager.findUniqueBy("clientId", clientId);

        if (openApp == null) {
            openApp = new OpenApp();
        }

        openApp.setGroupCode(groupCode);
        openApp.setCode(code);
        openApp.setName(name);
        openApp.setClientId(clientId);
        openApp.setClientSecret(clientSecret);
        openApp.setUserId(userId);
        openApp.setStatus("active");
        openApp.setCreateTime(new Date());
        openAppManager.save(openApp);
    }

    public void setOpenAppManager(OpenAppManager openAppManager) {
        this.openAppManager = openAppManager;
    }
}
