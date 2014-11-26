package com.mossle.msg.support;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.notification.NotificationDTO;
import com.mossle.api.notification.NotificationHandler;

import com.mossle.msg.domain.MsgInfo;
import com.mossle.msg.manager.MsgInfoManager;

public class MsgNotificationHandler implements NotificationHandler {
    private MsgInfoManager msgInfoManager;

    public void handle(NotificationDTO notificationDto) {
        if (!"userid".equals(notificationDto.getReceiverType())) {
            return;
        }

        MsgInfo msgInfo = new MsgInfo();
        msgInfo.setName(notificationDto.getSubject());
        msgInfo.setContent(notificationDto.getContent());
        msgInfo.setReceiverId(notificationDto.getReceiver());
        msgInfo.setSenderId("");
        msgInfo.setCreateTime(new Date());
        msgInfo.setStatus(0);
        msgInfoManager.save(msgInfo);
    }

    public String getType() {
        return "msg";
    }

    @Resource
    public void setMsgInfoManager(MsgInfoManager msgInfoManager) {
        this.msgInfoManager = msgInfoManager;
    }
}
