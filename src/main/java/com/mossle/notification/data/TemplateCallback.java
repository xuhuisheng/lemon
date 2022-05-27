package com.mossle.notification.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.notification.persistence.domain.NotificationTemplate;
import com.mossle.notification.persistence.manager.NotificationTemplateManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(TemplateCallback.class);
    private NotificationTemplateManager notificationTemplateManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String code = list.get(0);
        String name = list.get(1);
        String content = list.get(2);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateNotificationTemplate(code, name, content, lineNo);
    }

    public void createOrUpdateNotificationTemplate(String code, String name,
            String content, int lineNo) {
        NotificationTemplate notificationTemplate = notificationTemplateManager
                .findUniqueBy("code", code);

        if (notificationTemplate == null) {
            // insert
            notificationTemplate = new NotificationTemplate();
            notificationTemplate.setCode(code);
            notificationTemplate.setName(name);
            notificationTemplate.setContent(content);
            notificationTemplate.setStatus("active");
            // notificationTemplate.setTenantId(defaultTenantId);
            notificationTemplateManager.save(notificationTemplate);

            return;
        }
    }

    // ~
    public void setNotificationTemplateManager(
            NotificationTemplateManager notificationTemplateManager) {
        this.notificationTemplateManager = notificationTemplateManager;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }
}
