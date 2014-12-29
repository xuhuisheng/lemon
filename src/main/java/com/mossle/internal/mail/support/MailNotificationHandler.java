package com.mossle.internal.mail.support;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.notification.NotificationDTO;
import com.mossle.api.notification.NotificationHandler;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.internal.mail.service.MailDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailNotificationHandler implements NotificationHandler {
    private static Logger logger = LoggerFactory
            .getLogger(MailNotificationHandler.class);
    private MailDataService mailDataService;
    private UserConnector userConnector;

    public void handle(NotificationDTO notificationDto) {
        String email = null;

        if ("userid".equals(notificationDto.getReceiverType())) {
            email = userConnector.findById(notificationDto.getReceiver())
                    .getEmail();
        } else if ("email".equals(notificationDto.getReceiverType())) {
            email = notificationDto.getReceiver();
        } else {
            return;
        }

        try {
            mailDataService.send(email, notificationDto.getSubject(),
                    notificationDto.getContent(), "1");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public String getType() {
        return "mail";
    }

    @Resource
    public void setMailDataService(MailDataService mailDataService) {
        this.mailDataService = mailDataService;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
