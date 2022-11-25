package com.mossle.security.subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.mossle.client.mq.MqConsumer;

import com.mossle.security.client.ResourceDetailsMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.util.Assert;

public class ResourceSubscriber implements Runnable {
    private static Logger logger = LoggerFactory
            .getLogger(ResourceSubscriber.class);
    private MqConsumer mqConsumer;
    private boolean active;
    private Thread thread;
    private String tenantId = "1";
    private ResourceDetailsMonitor resourceDetailsMonitor;
    private boolean skip = true;

    @PostConstruct
    public void afterPropertiesSet() {
        if (skip) {
            logger.info("skip security subscriber");

            return;
        }

        logger.info("enable security subscriber");

        Assert.notNull(tenantId);

        active = true;

        thread = new Thread(this);
        thread.start();
    }

    @PreDestroy
    public void close() {
        logger.debug("close");
        active = false;

        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void run() {
        logger.debug("start");

        List<String> topics = new ArrayList<String>();
        topics.add(this.getName());
        mqConsumer.subscribe(topics);

        while (active) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }

            try {
                List<String> messages = mqConsumer.receive(1000);
                logger.debug("consume : {}", messages);
                this.handleMessages(messages);
                mqConsumer.commitSync();
            } catch (Exception ex) {
                logger.info(ex.getMessage(), ex);
            }
        }

        logger.debug("end");
    }

    public void handleMessages(List<String> messages) {
        for (String message : messages) {
            handleMessage(message);
        }
    }

    public void handleMessage(String message) {
        if (tenantId.equals(message)) {
            resourceDetailsMonitor.refresh();
        }
    }

    public boolean isTopic() {
        return true;
    }

    public String getName() {
        return "topic.security.resource";
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Resource
    public void setResourceDetailsMonitor(
            ResourceDetailsMonitor resourceDetailsMonitor) {
        this.resourceDetailsMonitor = resourceDetailsMonitor;
    }

    @Resource
    public void setMqConsumer(MqConsumer mqConsumer) {
        this.mqConsumer = mqConsumer;
    }

    @Value("${security.subscribe.skip:true}")
    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
