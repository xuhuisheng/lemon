package com.mossle.notification.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.page.Page;

import com.mossle.notification.component.EmailProcessor;
import com.mossle.notification.component.MsgProcessor;
import com.mossle.notification.persistence.domain.NotificationCatalog;
import com.mossle.notification.persistence.domain.NotificationConfig;
import com.mossle.notification.persistence.domain.NotificationMessage;
import com.mossle.notification.persistence.domain.NotificationProvider;
import com.mossle.notification.persistence.domain.NotificationQueue;
import com.mossle.notification.persistence.manager.NotificationCatalogManager;
import com.mossle.notification.persistence.manager.NotificationConfigManager;
import com.mossle.notification.persistence.manager.NotificationMessageManager;
import com.mossle.notification.persistence.manager.NotificationProviderManager;
import com.mossle.notification.persistence.manager.NotificationQueueManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationService {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationService.class);
    private NotificationMessageManager notificationMessageManager;
    private NotificationConfigManager notificationConfigManager;
    private NotificationCatalogManager notificationCatalogManager;
    private NotificationQueueManager notificationQueueManager;
    private NotificationProviderManager notificationProviderManager;
    private MsgProcessor msgProcessor;
    private EmailProcessor emailProcessor;

    public void receive(String requestId, String catalog, String to,
            String templateCode, String data, int priority, String configCode) {
        NotificationConfig notificationConfig = notificationConfigManager
                .findUniqueBy("code", configCode);
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setRequestKey(requestId);
        notificationMessage.setDestination(to);
        notificationMessage.setTemplateCode(templateCode);
        notificationMessage.setContent(data);
        notificationMessage.setCatalog(catalog);
        notificationMessage.setPriority(priority);
        notificationMessage.setNotificationConfig(notificationConfig);
        notificationMessage.setStatus("active");
        notificationMessage.setCreateTime(new Date());
        notificationMessageManager.save(notificationMessage);
    }

    public void preprocess() {
        String hql = "from NotificationMessage where notificationQueue=null order by id";
        Page page = notificationMessageManager.pagedQuery(hql, 1, 100);
        List<NotificationMessage> notificationMessages = (List<NotificationMessage>) page
                .getResult();

        for (NotificationMessage notificationMessage : notificationMessages) {
            String queueHql = "from NotificationQueue where notificationConfig=? and priority<=?";
            NotificationQueue notificationQueue = notificationQueueManager
                    .findUnique(queueHql,
                            notificationMessage.getNotificationConfig(),
                            notificationMessage.getPriority());

            if (notificationQueue == null) {
                logger.info("cannot find queue : {} {} {}", notificationMessage
                        .getId(), notificationMessage.getNotificationConfig()
                        .getId(), notificationMessage.getPriority());
                notificationMessage.setStatus("error");
            } else {
                notificationMessage.setNotificationQueue(notificationQueue);
            }

            notificationMessageManager.save(notificationMessage);
        }
    }

    public void sendMessages() {
        String hql = "from NotificationQueue where status='active' order by priority desc";
        List<NotificationQueue> notificationQueues = notificationQueueManager
                .find(hql);

        for (NotificationQueue notificationQueue : notificationQueues) {
            String messageHql = "from NotificationMessage where notificationQueue=? and status='active' order by id";
            Page page = notificationMessageManager.pagedQuery(messageHql, 1,
                    notificationQueue.getWeight(), notificationQueue);

            List<NotificationMessage> notificationMessages = (List<NotificationMessage>) page
                    .getResult();
            String hqlCatalog = "select queue.notificationConfig.notificationCatalog "
                    + " from NotificationQueue queue where queue=?";
            NotificationCatalog notificationCatalog = notificationCatalogManager
                    .findUnique(hqlCatalog, notificationQueue);
            NotificationProvider notificationProvider = this
                    .selectProvider(notificationCatalog.getCode());

            for (NotificationMessage notificationMessage : notificationMessages) {
                this.processOneMessage(notificationMessage,
                        notificationProvider);
            }
        }
    }

    public void processOneMessage(NotificationMessage notificationMessage,
            NotificationProvider notificationProvider) {
        if ("msg".equals(notificationMessage.getCatalog())) {
            this.msgProcessor
                    .process(notificationMessage, notificationProvider);
        } else if ("email".equals(notificationMessage.getCatalog())) {
            this.emailProcessor.process(notificationMessage,
                    notificationProvider);
        } else {
            logger.info("cannot process {} {}", notificationMessage.getId(),
                    notificationMessage.getCatalog());
            notificationMessage.setStatus("error");
        }

        notificationMessageManager.save(notificationMessage);
    }

    public NotificationProvider selectProvider(String catalog) {
        String hql = "from NotificationProvider where notificationCatalog.code=?";

        return notificationProviderManager.findUnique(hql, catalog);
    }

    // ~
    @Resource
    public void setNotificationMessageManager(
            NotificationMessageManager notificationMessageManager) {
        this.notificationMessageManager = notificationMessageManager;
    }

    @Resource
    public void setNotificationConfigManager(
            NotificationConfigManager notificationConfigManager) {
        this.notificationConfigManager = notificationConfigManager;
    }

    @Resource
    public void setNotificationCatalogManager(
            NotificationCatalogManager notificationCatalogManager) {
        this.notificationCatalogManager = notificationCatalogManager;
    }

    @Resource
    public void setNotificationQueueManager(
            NotificationQueueManager notificationQueueManager) {
        this.notificationQueueManager = notificationQueueManager;
    }

    @Resource
    public void setNotificationProviderManager(
            NotificationProviderManager notificationProviderManager) {
        this.notificationProviderManager = notificationProviderManager;
    }

    @Resource
    public void setMsgProcessor(MsgProcessor msgProcessor) {
        this.msgProcessor = msgProcessor;
    }

    @Resource
    public void setEmailProcessor(EmailProcessor emailProcessor) {
        this.emailProcessor = emailProcessor;
    }
}
