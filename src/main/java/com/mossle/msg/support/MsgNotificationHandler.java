package com.mossle.msg.support;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.notification.NotificationDTO;
import com.mossle.api.notification.NotificationHandler;

import com.mossle.msg.persistence.domain.MsgInfo;
import com.mossle.msg.persistence.manager.MsgInfoManager;

import org.apache.commons.lang3.StringUtils;

public class MsgNotificationHandler implements NotificationHandler {
    private MsgInfoManager msgInfoManager;

    public void handle(NotificationDTO notificationDto, String tenantId) {
        if (!"userid".equals(notificationDto.getReceiverType())) {
            return;
        }

        MsgInfo msgInfo = new MsgInfo();
        msgInfo.setName(notificationDto.getSubject());
        msgInfo.setContent(notificationDto.getContent());
        msgInfo.setReceiverId(notificationDto.getReceiver());
        msgInfo.setCreateTime(new Date());
        msgInfo.setStatus(0);
        msgInfo.setTenantId(tenantId);

        if (StringUtils.isNotBlank(notificationDto.getSender())) {
            msgInfo.setSenderId(notificationDto.getSender());
        } else {
            msgInfo.setSenderId("");
        }

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
