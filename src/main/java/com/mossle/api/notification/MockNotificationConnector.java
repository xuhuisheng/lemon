package com.mossle.api.notification;

import java.util.Collection;

public class MockNotificationConnector implements NotificationConnector {
    public void send(NotificationDTO notificationDto, String tenantId) {
    }

    public Collection<String> getTypes(String tenantId) {
        return null;
    }
}
