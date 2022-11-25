package com.mossle.security.subscribe;

import javax.annotation.Resource;

import com.mossle.client.mq.MqProducer;

import com.mossle.spi.auth.ResourcePublisher;

import org.springframework.util.Assert;

public class ResourcePublisherImpl implements ResourcePublisher {
    private MqProducer mqProducer;
    private String tenantId = "1";

    @Override
    public void publish(String message) {
        mqProducer.send(getTopic(), message);
    }

    @Override
    public void publish() {
        this.publish(tenantId);
    }

    public String getTopic() {
        return "topic.security.resource";
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Resource
    public void setMqProducer(MqProducer mqProducer) {
        this.mqProducer = mqProducer;
    }
}
