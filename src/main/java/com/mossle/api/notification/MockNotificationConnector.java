package com.mossle.api.notification;

import java.util.Collection;

public class MockNotificationConnector implements NotificationConnector {
    public void send(NotificationDTO notificationDto) {
    }

    public Collection<String> getTypes() {
        return null;
    }
}
