package com.mossle.notification.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.notification.persistence.manager.NotificationCatalogManager;
import com.mossle.notification.persistence.manager.NotificationConfigManager;
import com.mossle.notification.persistence.manager.NotificationProviderManager;
import com.mossle.notification.persistence.manager.NotificationQueueManager;
import com.mossle.notification.persistence.manager.NotificationTemplateManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationDeployer.class);
    private NotificationCatalogManager notificationCatalogManager;
    private NotificationProviderManager notificationProviderManager;
    private NotificationTemplateManager notificationTemplateManager;
    private NotificationConfigManager notificationConfigManager;
    private NotificationQueueManager notificationQueueManager;
    private String dataFilePathCatalog = "data/notification/catalog.csv";
    private String dataFileEncodingCatalog = "GB2312";
    private String dataFilePathProvider = "data/notification/provider.csv";
    private String dataFileEncodingProvider = "GB2312";
    private String dataFilePathTemplate = "data/notification/template.csv";
    private String dataFileEncodingTemplate = "GB2312";
    private String dataFilePathConfig = "data/notification/config.csv";
    private String dataFileEncodingConfig = "GB2312";
    private String dataFilePathQueue = "data/notification/queue.csv";
    private String dataFileEncodingQueue = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init notification data");

            return;
        }

        // catalog
        CatalogCallback catalogCallback = new CatalogCallback();
        catalogCallback
                .setNotificationCatalogManager(notificationCatalogManager);
        catalogCallback.setDefaultTenantId(defaultTenantId);
        new CsvProcessor().process(dataFilePathCatalog,
                dataFileEncodingCatalog, catalogCallback);

        // provider
        ProviderCallback providerCallback = new ProviderCallback();
        providerCallback
                .setNotificationProviderManager(notificationProviderManager);
        providerCallback
                .setNotificationCatalogManager(notificationCatalogManager);
        providerCallback.setDefaultTenantId(defaultTenantId);
        new CsvProcessor().process(dataFilePathProvider,
                dataFileEncodingProvider, providerCallback);

        // template
        TemplateCallback templateCallback = new TemplateCallback();
        templateCallback
                .setNotificationTemplateManager(notificationTemplateManager);
        templateCallback.setDefaultTenantId(defaultTenantId);
        new CsvProcessor().process(dataFilePathTemplate,
                dataFileEncodingTemplate, templateCallback);

        // config
        ConfigCallback configCallback = new ConfigCallback();
        configCallback.setNotificationConfigManager(notificationConfigManager);
        configCallback
                .setNotificationCatalogManager(notificationCatalogManager);
        configCallback.setDefaultTenantId(defaultTenantId);
        new CsvProcessor().process(dataFilePathConfig, dataFileEncodingConfig,
                configCallback);

        // queue
        QueueCallback queueCallback = new QueueCallback();
        queueCallback.setNotificationQueueManager(notificationQueueManager);
        queueCallback.setNotificationConfigManager(notificationConfigManager);
        queueCallback.setDefaultTenantId(defaultTenantId);
        new CsvProcessor().process(dataFilePathQueue, dataFileEncodingQueue,
                queueCallback);
    }

    // ~
    @Resource
    public void setNotificationCatalogManager(
            NotificationCatalogManager notificationCatalogManager) {
        this.notificationCatalogManager = notificationCatalogManager;
    }

    @Resource
    public void setNotificationProviderManager(
            NotificationProviderManager notificationProviderManager) {
        this.notificationProviderManager = notificationProviderManager;
    }

    @Resource
    public void setNotificationTemplateManager(
            NotificationTemplateManager notificationTemplateManager) {
        this.notificationTemplateManager = notificationTemplateManager;
    }

    @Resource
    public void setNotificationConfigManager(
            NotificationConfigManager notificationConfigManager) {
        this.notificationConfigManager = notificationConfigManager;
    }

    @Resource
    public void setNotificationQueueManager(
            NotificationQueueManager notificationQueueManager) {
        this.notificationQueueManager = notificationQueueManager;
    }
}
