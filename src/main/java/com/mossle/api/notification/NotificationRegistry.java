package com.mossle.api.notification;

public interface NotificationRegistry {
    void register(NotificationHandler notificationHandler);

    void unregister(NotificationHandler notificationHandler);
}
