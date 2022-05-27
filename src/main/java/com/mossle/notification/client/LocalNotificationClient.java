package com.mossle.notification.client;

import javax.annotation.Resource;

import com.mossle.client.notification.NotificationClient;

import com.mossle.core.util.BaseDTO;

import com.mossle.notification.service.NotificationService;

public class LocalNotificationClient implements NotificationClient {
    private NotificationService notificationService;

    public BaseDTO send(String requestId, String catalog, String to,
            String templateCode, String data, int priority, String config) {
        notificationService.receive(requestId, catalog, to, templateCode, data,
                priority, config);

        BaseDTO baseDto = new BaseDTO();

        return baseDto;
    }

    @Resource
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
