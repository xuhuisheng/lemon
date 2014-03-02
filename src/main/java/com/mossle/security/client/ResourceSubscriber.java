package com.mossle.security.client;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.ext.message.Subscribable;

import org.springframework.util.Assert;

public class ResourceSubscriber implements Subscribable<String> {
    private String scopeId = "1";
    private ResourceDetailsMonitor resourceDetailsMonitor;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(scopeId);
    }

    public void handleMessage(String message) {
        if (scopeId.equals(message)) {
            resourceDetailsMonitor.refresh();
        }
    }

    public String getTopic() {
        return "topic.security.resource";
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    @Resource
    public void setResourceDetailsMonitor(
            ResourceDetailsMonitor resourceDetailsMonitor) {
        this.resourceDetailsMonitor = resourceDetailsMonitor;
    }
}
