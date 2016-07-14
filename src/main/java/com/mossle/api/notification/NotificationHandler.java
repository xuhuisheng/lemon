package com.mossle.api.notification;

public interface NotificationHandler {
    String getType();

    void handle(NotificationDTO notificationDto, String tenantId);
}
