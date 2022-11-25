package com.mossle.user.subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.client.mq.MqConsumer;

import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.user.subscribe.UserSubscriber")
public class UserSubscriber implements Runnable {
    private static Logger logger = LoggerFactory
            .getLogger(UserSubscriber.class);
    private MqConsumer mqConsumer;
    private JsonMapper jsonMapper = new JsonMapper();
    private boolean active;
    private Thread thread;
    private boolean skip = false;
    private UserCache userCache;

    @PostConstruct
    public void init() {
        if (skip) {
            logger.info("skip user subscriber");

            return;
        }

        logger.info("enable user subscriber");
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
        topics.add("mossle_user_create");
        topics.add("mossle_user_update");
        topics.add("mossle_user_remove");
        mqConsumer.subscribe(topics);

        while (active) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }

            try {
                List<String> messages = mqConsumer.receive(1000);
                logger.debug("consume : {}", messages);
                handleMessages(messages);
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
        try {
            UserDTO userDto = jsonMapper.fromJson(message, UserDTO.class);
            userCache.removeUser(userDto);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Resource
    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    @Resource
    public void setMqConsumer(MqConsumer mqConsumer) {
        this.mqConsumer = mqConsumer;
    }

    @Value("${user.subscribe.skip:true}")
    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
