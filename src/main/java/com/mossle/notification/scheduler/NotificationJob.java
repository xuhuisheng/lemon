package com.mossle.notification.scheduler;

import javax.annotation.Resource;

import com.mossle.notification.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

@Component
public class NotificationJob {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationJob.class);

    // private boolean running;
    private boolean enabled = true;
    private NotificationService notificationService;

    // every 5 seconds
    @Scheduled(cron = "0/5 * * * * ?")
    public void preprocess() {
        if (!enabled) {
            return;
        }

        try {
            notificationService.preprocess();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    // every 10 seconds
    @Scheduled(cron = "0/10 * * * * ?")
    public void sendMessages() {
        if (!enabled) {
            return;
        }

        logger.debug("start");

        try {
            notificationService.sendMessages();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        logger.debug("end");
    }

    // ~
    @Resource
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Value("${notification.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
