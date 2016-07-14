package com.mossle.core.subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.jms.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

public class SubscribeProcessor implements ApplicationContextAware {
    private static Logger logger = LoggerFactory
            .getLogger(SubscribeProcessor.class);
    private ApplicationContext applicationContext;
    private ConnectionFactory connectionFactory;
    private List<DefaultMessageListenerContainer> defaultMessageListenerContainers = new ArrayList<DefaultMessageListenerContainer>();
    private Map<String, CompositeSubscriber> compositeMap = new HashMap<String, CompositeSubscriber>();
    private Properties properties;
    private boolean enabled = true;
    private String prefix = "subscribe.";

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        if (!enabled) {
            return;
        }

        this.buildComposite();

        for (DefaultMessageListenerContainer defaultMessageListenerContainer : defaultMessageListenerContainers) {
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

    public void buildComposite() throws Exception {
        if (properties == null) {
            return;
        }

        Map<String, String> map = new HashMap<String, String>();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if ("subscribe.enabled".equals(key)) {
                continue;
            }

            if (key.startsWith(prefix)) {
                String name = key.substring(prefix.length());
                map.put(name, value);
                logger.debug("{} : {}", name, map.get(name));
            }
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String subscribeClassName = entry.getKey();
            String subscribeName = entry.getValue();

            Subscribable subscribable = (Subscribable) applicationContext
                    .getBean(Class.forName(subscribeClassName));
            logger.info("subscribable : " + subscribable);

            CompositeSubscriber compositeSubscriber = compositeMap
                    .get(subscribeName);

            if (compositeSubscriber == null) {
                compositeSubscriber = new CompositeSubscriber();
                compositeMap.put(subscribeName, compositeSubscriber);
            }

            compositeSubscriber.addSubscribable(subscribable);

            DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
            defaultMessageListenerContainers
                    .add(defaultMessageListenerContainer);
            defaultMessageListenerContainer.setPubSubDomain(subscribable
                    .isTopic());
            defaultMessageListenerContainer
                    .setConnectionFactory(connectionFactory);
            defaultMessageListenerContainer.setDestinationName(subscribable
                    .getName());

            MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
            messageListenerAdapter.setDelegate(compositeSubscriber);
            defaultMessageListenerContainer
                    .setMessageListener(messageListenerAdapter);

            // defaultMessageListenerContainer.afterPropertiesSet();
            // defaultMessageListenerContainer.start();
        }
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static class CompositeSubscriber {
        private List<Subscribable> subscribables = new ArrayList<Subscribable>();

        public void handleMessage(String text) {
            for (Subscribable subscribable : subscribables) {
                subscribable.handleMessage(text);
            }
        }

        public void addSubscribable(Subscribable subscribable) {
            subscribables.add(subscribable);
        }
    }
}
