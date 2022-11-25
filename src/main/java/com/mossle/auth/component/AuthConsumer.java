package com.mossle.auth.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.mossle.client.authz.AuthzResourceHelper;
import com.mossle.client.mq.MqConsumer;

import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

// @Component
public class AuthConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(AuthConsumer.class);
    private MqConsumer mqConsumer;
    private AuthzResourceHelper authzResourceHelper;
    private JsonMapper jsonMapper = new JsonMapper();
    private boolean active;
    private Thread thread;

    @PostConstruct
    public void init() {
        logger.debug("init");
        active = true;

        thread = new Thread(this);
        thread.start();
    }

    @PreDestroy
    public void close() {
        logger.debug("close");
        active = false;
        thread.interrupt();
    }

    public void run() {
        logger.debug("start");

        List<String> topics = new ArrayList<String>();
        topics.add("mossle_auth_update");
        mqConsumer.subscribe(topics);

        while (active) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
            }

            try {
                List<String> messages = mqConsumer.receive(5000);
                logger.debug("consume : {}", messages);
                // update cache
                authzResourceHelper.refresh();
                mqConsumer.commitSync();
            } catch (Exception ex) {
                logger.info(ex.getMessage(), ex);
            }
        }

        logger.debug("end");
    }

    @Resource
    public void setMqConsumer(MqConsumer mqConsumer) {
        this.mqConsumer = mqConsumer;
    }

    @Resource
    public void setAuthzResourceHelper(AuthzResourceHelper authzResourceHelper) {
        this.authzResourceHelper = authzResourceHelper;
    }
}
