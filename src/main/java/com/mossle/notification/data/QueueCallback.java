package com.mossle.notification.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.notification.persistence.domain.NotificationConfig;
import com.mossle.notification.persistence.domain.NotificationQueue;
import com.mossle.notification.persistence.manager.NotificationConfigManager;
import com.mossle.notification.persistence.manager.NotificationQueueManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueCallback implements CsvCallback {
    private static Logger logger = LoggerFactory.getLogger(QueueCallback.class);
    private NotificationConfigManager notificationConfigManager;
    private NotificationQueueManager notificationQueueManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String code = list.get(0);
        String name = list.get(1);
        int priority = Integer.parseInt(list.get(2));
        int weight = Integer.parseInt(list.get(3));
        String config = list.get(4);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateNotificationQueue(code, name, priority, weight,
                config, lineNo);
    }

    public void createOrUpdateNotificationQueue(String code, String name,
            int priority, int weight, String config, int lineNo) {
        NotificationQueue notificationQueue = notificationQueueManager
                .findUniqueBy("code", code);

        NotificationConfig notificationConfig = notificationConfigManager
                .findUniqueBy("code", config);

        if (notificationQueue == null) {
            // insert
            notificationQueue = new NotificationQueue();
            notificationQueue.setCode(code);
            notificationQueue.setName(name);
            notificationQueue.setPriority(priority);
            notificationQueue.setWeight(weight);
            notificationQueue.setStatus("active");
            // notificationQueue.setTenantId(defaultTenantId);
            notificationQueue.setNotificationConfig(notificationConfig);
            notificationQueueManager.save(notificationQueue);

            return;
        }
    }

    // ~
    public void setNotificationConfigManager(
            NotificationConfigManager notificationConfigManager) {
        this.notificationConfigManager = notificationConfigManager;
    }

    public void setNotificationQueueManager(
            NotificationQueueManager notificationQueueManager) {
        this.notificationQueueManager = notificationQueueManager;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }
}
