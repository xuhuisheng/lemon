package com.mossle.notification.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.notification.persistence.domain.NotificationCatalog;
import com.mossle.notification.persistence.manager.NotificationCatalogManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(CatalogCallback.class);
    private NotificationCatalogManager notificationCatalogManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String code = list.get(0);
        String name = list.get(1);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateNotificationCatalog(code, name, lineNo);
    }

    public void createOrUpdateNotificationCatalog(String code, String name,
            int lineNo) {
        NotificationCatalog notificationCatalog = notificationCatalogManager
                .findUniqueBy("code", code);

        if (notificationCatalog == null) {
            // insert
            notificationCatalog = new NotificationCatalog();
            notificationCatalog.setCode(code);
            notificationCatalog.setName(name);
            notificationCatalog.setStatus("active");
            // notificationCatalog.setTenantId(defaultTenantId);
            notificationCatalogManager.save(notificationCatalog);

            return;
        }
    }

    public void setNotificationCatalogManager(
            NotificationCatalogManager notificationCatalogManager) {
        this.notificationCatalogManager = notificationCatalogManager;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }
}
