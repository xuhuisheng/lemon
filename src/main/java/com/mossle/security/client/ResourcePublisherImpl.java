package com.mossle.security.client;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.jms.ConnectionFactory;

import com.mossle.spi.auth.ResourcePublisher;

import org.springframework.jms.core.JmsTemplate;

import org.springframework.util.Assert;

public class ResourcePublisherImpl implements ResourcePublisher {
    private ConnectionFactory connectionFactory;
    private JmsTemplate jmsTemplate;
    private String tenantId = "1";

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
        this.publish(tenantId);
    }

    public String getTopic() {
        return "topic.security.resource";
    }

    @Resource
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
