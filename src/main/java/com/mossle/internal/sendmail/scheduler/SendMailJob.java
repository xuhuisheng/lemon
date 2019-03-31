package com.mossle.internal.sendmail.scheduler;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantConnector;

import com.mossle.core.mail.MailHelper;
import com.mossle.core.mapper.BeanMapper;

import com.mossle.internal.sendmail.persistence.domain.SendmailApp;
import com.mossle.internal.sendmail.persistence.domain.SendmailQueue;
import com.mossle.internal.sendmail.persistence.manager.SendmailAppManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailQueueManager;
import com.mossle.internal.sendmail.service.SendmailDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SendMailJob {
    private static Logger logger = LoggerFactory.getLogger(SendMailJob.class);
    private SendmailAppManager sendmailAppManager;
    private SendmailQueueManager sendmailQueueManager;
    private SendmailDataService sendmailDataService;
    private int threshold = 20;
    private BeanMapper beanMapper = new BeanMapper();
    private MailHelper mailHelper;
    private boolean running;
    private boolean enabled = true;
    private TenantConnector tenantConnector;

    // every 10 seconds
    @Scheduled(cron = "0/10 * * * * ?")
    public void execute() {
        if (!enabled) {
            return;
        }

        try {
            for (SendmailApp sendmailApp : sendmailAppManager.getAll()) {
                this.doExecute(sendmailApp);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public synchronized void doExecute(SendmailApp sendmailApp)
            throws Exception {
        if (running) {
            return;
        }

        running = true;
        logger.debug("send mail job start");

        List<SendmailQueue> sendmailQueues = sendmailDataService
                .findTopSendmailQueues(sendmailApp);
        logger.debug("sendmailQueues : {}", sendmailQueues.size());

        for (SendmailQueue sendmailQueue : sendmailQueues) {
            sendmailDataService.processSendmailQueue(sendmailQueue);
        }

        logger.debug("send mail job end");
        running = false;
    }

    // ~
    @Resource
    public void setSendmailAppManager(SendmailAppManager sendmailAppManager) {
        this.sendmailAppManager = sendmailAppManager;
    }

    @Resource
    public void setSendmailQueueManager(
            SendmailQueueManager sendmailQueueManager) {
        this.sendmailQueueManager = sendmailQueueManager;
    }

    @Resource
    public void setSendmailDataService(SendmailDataService sendmailDataService) {
        this.sendmailDataService = sendmailDataService;
    }

    @Resource
    public void setMailHelper(MailHelper mailHelper) {
        this.mailHelper = mailHelper;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Value("${sendmail.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }
}
