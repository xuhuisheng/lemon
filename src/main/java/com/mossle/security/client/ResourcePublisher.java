package com.mossle.security.client;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.jms.ConnectionFactory;

import org.springframework.jms.core.JmsTemplate;

import org.springframework.util.Assert;

public class ResourcePublisher {
    private ConnectionFactory connectionFactory;
    private JmsTemplate jmsTemplate;
    private String scopeId = "1";

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(connectionFactory);
        jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
    }

    public void publish(String message) {
        jmsTemplate.convertAndSend(getTopic(), message);
    }

    public void publish() {
        this.publish(scopeId);
    }

    public String getTopic() {
        return "topic.security.resource";
    }

    @Resource
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }
}
