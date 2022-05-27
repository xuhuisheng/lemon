package com.mossle.notification.component;

import javax.annotation.Resource;

import com.mossle.client.notification.SendMsgClient;

import com.mossle.notification.persistence.domain.NotificationMessage;
import com.mossle.notification.persistence.domain.NotificationProvider;

import org.springframework.stereotype.Component;

@Component
public class MsgProcessor {
    private SendMsgClient sendMsgClient;

    public void process(NotificationMessage notificationMessage,
            NotificationProvider notificationProvider) {
        String from = notificationMessage.getApp();
        String to = notificationMessage.getDestination();
        String content = notificationMessage.getContent();
        sendMsgClient.sendMsg(from, to, content);
        notificationMessage.setStatus("success");
    }

    @Resource
    public void setSendMsgClient(SendMsgClient sendMsgClient) {
        this.sendMsgClient = sendMsgClient;
    }
}
