package com.mossle.notification.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.notification.persistence.domain.NotificationCatalog;
import com.mossle.notification.persistence.domain.NotificationConfig;
import com.mossle.notification.persistence.manager.NotificationCatalogManager;
import com.mossle.notification.persistence.manager.NotificationConfigManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(ConfigCallback.class);
    private NotificationConfigManager notificationConfigManager;
    private NotificationCatalogManager notificationCatalogManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String code = list.get(0);
        String name = list.get(1);
        String app = list.get(2);
        String catalog = list.get(3);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateNotificationConfig(code, name, app, catalog, lineNo);
    }

    public void createOrUpdateNotificationConfig(String code, String name,
            String app, String catalog, int lineNo) {
        NotificationConfig notificationConfig = notificationConfigManager
                .findUniqueBy("code", code);

        NotificationCatalog notificationCatalog = notificationCatalogManager
                .findUniqueBy("code", catalog);

        if (notificationConfig == null) {
            // insert
            notificationConfig = new NotificationConfig();
            notificationConfig.setCode(code);
            notificationConfig.setName(name);
            notificationConfig.setApp(app);
            notificationConfig.setStatus("active");
            // notificationProvider.setTenantId(defaultTenantId);
            notificationConfig.setNotificationCatalog(notificationCatalog);
            notificationConfigManager.save(notificationConfig);

            return;
        }
    }

    // ~
    public void setNotificationConfigManager(
            NotificationConfigManager notificationConfigManager) {
        this.notificationConfigManager = notificationConfigManager;
    }

    public void setNotificationCatalogManager(
            NotificationCatalogManager notificationCatalogManager) {
        this.notificationCatalogManager = notificationCatalogManager;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }
}
