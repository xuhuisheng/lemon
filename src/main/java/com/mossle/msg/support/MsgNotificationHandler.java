package com.mossle.msg.support;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.notification.NotificationDTO;
import com.mossle.api.notification.NotificationHandler;

import com.mossle.msg.persistence.domain.MsgInfo;
import com.mossle.msg.persistence.manager.MsgInfoManager;

import org.apache.commons.lang3.StringUtils;

public class MsgNotificationHandler implements NotificationHandler {
    private MsgInfoManager msgInfoManager;
    private String defaultSender = "";

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

        String humanTaskId = (String) notificationDto.getData().get(
                "humanTaskId");
        msgInfo.setData(humanTaskId);

        if (StringUtils.isNotBlank(notificationDto.getSender())) {
            msgInfo.setSenderId(notificationDto.getSender());
        } else {
            msgInfo.setSenderId(defaultSender);
        }

        msgInfoManager.save(msgInfo);
    }

    public String getType() {
        return "msg";
    }

    public void setDefaultSender(String defaultSender) {
        this.defaultSender = defaultSender;
    }

    @Resource
    public void setMsgInfoManager(MsgInfoManager msgInfoManager) {
        this.msgInfoManager = msgInfoManager;
    }
}
