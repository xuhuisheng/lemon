package com.mossle.internal.mail.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import javax.mail.internet.MimeMessage;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.ext.mail.MailHelper;

import com.mossle.internal.mail.domain.MailConfig;
import com.mossle.internal.mail.domain.MailHistory;
import com.mossle.internal.mail.domain.MailQueue;
import com.mossle.internal.mail.service.MailDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SendMailJob {
    private static Logger logger = LoggerFactory.getLogger(SendMailJob.class);
    private MailDataService mailDataService;
    private int threshold = 20;
    private BeanMapper beanMapper = new BeanMapper();
    private MailHelper mailHelper;
    private boolean running;
    private boolean enabled = true;

    // every 10 seconds
    @Scheduled(cron = "0/10 * * * * ?")
    public void execute() {
        if (!enabled) {
            return;
        }

        try {
            this.doExecute();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public synchronized void doExecute() throws Exception {
        if (running) {
            return;
        }

        running = true;
        logger.debug("send mail job start");

        List<MailQueue> mailQueues = mailDataService
                .findTopMailQueues(threshold);
        logger.debug("mailQueues : {}", mailQueues.size());

        for (MailQueue mailQueue : mailQueues) {
            mailDataService.processMailQueue(mailQueue);
        }

        logger.debug("send mail job end");
        running = false;
    }

    @Resource
    public void setMailDataService(MailDataService mailDataService) {
        this.mailDataService = mailDataService;
    }

    @Resource
    public void setMailHelper(MailHelper mailHelper) {
        this.mailHelper = mailHelper;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Value("${mail.enabled}")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
