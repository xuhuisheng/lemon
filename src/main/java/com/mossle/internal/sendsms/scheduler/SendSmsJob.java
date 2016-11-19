package com.mossle.internal.sendsms.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.internal.sendsms.persistence.domain.SendsmsConfig;
import com.mossle.internal.sendsms.persistence.domain.SendsmsHistory;
import com.mossle.internal.sendsms.persistence.domain.SendsmsQueue;
import com.mossle.internal.sendsms.service.SendsmsDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SendSmsJob {
    private static Logger logger = LoggerFactory.getLogger(SendSmsJob.class);
    private SendsmsDataService sendsmsDataService;
    private int threshold = 20;
    private BeanMapper beanMapper = new BeanMapper();
    private boolean running;

    // every 10 seconds
    @Scheduled(cron = "0/10 * * * * ?")
    public void execute() throws Exception {
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
        logger.debug("send sms job start");

        List<SendsmsQueue> sendsmsQueues = sendsmsDataService
                .findTopSendsmsQueues(threshold);
        logger.debug("sendsmsQueues : {}", sendsmsQueues.size());

        for (SendsmsQueue sendsmsQueue : sendsmsQueues) {
            sendsmsDataService.processSendsmsQueue(sendsmsQueue);
        }

        logger.debug("send sms job end");
        running = false;
    }

    @Resource
    public void setSendsmsDataService(SendsmsDataService sendsmsDataService) {
        this.sendsmsDataService = sendsmsDataService;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
