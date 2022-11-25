package com.mossle.auth.subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.persistence.manager.UserStatusManager;

import com.mossle.client.mq.MqConsumer;

import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.auth.subscribe.UserSubscriber")
public class UserSubscriber implements Runnable {
    private static Logger logger = LoggerFactory
            .getLogger(UserSubscriber.class);
    private MqConsumer mqConsumer;
    private JsonMapper jsonMapper = new JsonMapper();
    private boolean active;
    private Thread thread;
    private boolean skip = false;
    private UserStatusManager userStatusManager;

    @PostConstruct
    public void init() {
        if (skip) {
            logger.info("skip auth subscriber");

            return;
        }

        logger.info("enable auth subscriber");
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
            UserEvent userEvent = jsonMapper.fromJson(message, UserEvent.class);

            if ("create".equals(userEvent.getName())) {
                this.createOrUpdateUser(userEvent.getCode(),
                        userEvent.getName());
            } else if ("update".equals(userEvent.getName())) {
                this.createOrUpdateUser(userEvent.getCode(),
                        userEvent.getName());
            } else if ("remove".equals(userEvent.getName())) {
                this.removeUser(userEvent.getCode());
            } else {
                logger.info("unsupport : {}", userEvent.getName());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void createOrUpdateUser(String code, String name) {
        String hql = "from UserStatus where ref=?";
        UserStatus userStatus = userStatusManager.findUnique(hql, code);

        if (userStatus == null) {
            return;
        }

        // update
        userStatus.setUsername(name);
        userStatusManager.save(userStatus);
        logger.info("update auth user : {}", code);
    }

    public void removeUser(String code) {
        String hql = "from UserStatus where ref=?";
        UserStatus userStatus = userStatusManager.findUnique(hql, code);

        if (userStatus == null) {
            return;
        }

        userStatusManager.remove(userStatus);
        logger.info("remove auth user : {}", code);
    }

    //
    @Resource
    public void setMqConsumer(MqConsumer mqConsumer) {
        this.mqConsumer = mqConsumer;
    }

    @Value("${auth.subscribe.skip:true}")
    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }
}
