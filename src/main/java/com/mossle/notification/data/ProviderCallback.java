package com.mossle.notification.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.notification.persistence.domain.NotificationCatalog;
import com.mossle.notification.persistence.domain.NotificationProvider;
import com.mossle.notification.persistence.manager.NotificationCatalogManager;
import com.mossle.notification.persistence.manager.NotificationProviderManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(ProviderCallback.class);
    private NotificationProviderManager notificationProviderManager;
    private NotificationCatalogManager notificationCatalogManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String code = list.get(0);
        String name = list.get(1);
        String catalog = list.get(2);
        String username = list.get(3);
        String password = list.get(4);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateNotificationProvider(code, name, catalog, username,
                password, lineNo);
    }

    public void createOrUpdateNotificationProvider(String code, String name,
            String catalog, String username, String password, int lineNo) {
        NotificationProvider notificationProvider = notificationProviderManager
                .findUniqueBy("code", code);

        NotificationCatalog notificationCatalog = notificationCatalogManager
                .findUniqueBy("code", catalog);

        if (notificationProvider == null) {
            // insert
            notificationProvider = new NotificationProvider();
            notificationProvider.setCode(code);
            notificationProvider.setName(name);
            notificationProvider.setUsername(username);
            notificationProvider.setPassword(password);
            notificationProvider.setStatus("active");
            // notificationProvider.setTenantId(defaultTenantId);
            notificationProvider.setNotificationCatalog(notificationCatalog);
            notificationProviderManager.save(notificationProvider);

            return;
        }
    }

    // ~
    public void setNotificationProviderManager(
            NotificationProviderManager notificationProviderManager) {
        this.notificationProviderManager = notificationProviderManager;
    }

    public void setNotificationCatalogManager(
            NotificationCatalogManager notificationCatalogManager) {
        this.notificationCatalogManager = notificationCatalogManager;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }
}
