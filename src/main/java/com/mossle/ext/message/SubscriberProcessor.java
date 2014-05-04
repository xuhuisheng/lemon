package com.mossle.ext.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import javax.jms.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

public class SubscriberProcessor implements ApplicationContextAware {
    private static Logger logger = LoggerFactory
            .getLogger(SubscriberProcessor.class);
    private ApplicationContext applicationContext;
    private ConnectionFactory connectionFactory;
    private List<DefaultMessageListenerContainer> defaultMessageListenerContainers = new ArrayList<DefaultMessageListenerContainer>();

    @PostConstruct
    public void afterPropertiesSet() {
        Map<String, Subscribable> subscribableMap = applicationContext
                .getBeansOfType(Subscribable.class);

        for (Subscribable subscribable : subscribableMap.values()) {
            logger.info("subscribable : " + subscribable);

            DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
            defaultMessageListenerContainers
                    .add(defaultMessageListenerContainer);
            defaultMessageListenerContainer.setPubSubDomain(true);
            defaultMessageListenerContainer
                    .setConnectionFactory(connectionFactory);
            defaultMessageListenerContainer.setDestinationName(subscribable
                    .getTopic());

            MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
            messageListenerAdapter.setDelegate(subscribable);
            defaultMessageListenerContainer
                    .setMessageListener(messageListenerAdapter);
            defaultMessageListenerContainer.afterPropertiesSet();
            defaultMessageListenerContainer.start();
        }
    }

    @PreDestroy
    public void destroy() {
        for (DefaultMessageListenerContainer defaultMessageListenerContainer : defaultMessageListenerContainers) {
            defaultMessageListenerContainer.destroy();
        }
    }

    @Resource
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
