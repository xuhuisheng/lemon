package com.mossle.security.client;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.subscribe.Subscribable;

import org.springframework.util.Assert;

public class ResourceSubscriber implements Subscribable<String> {
    private String tenantId = "1";
    private ResourceDetailsMonitor resourceDetailsMonitor;

    @PostConstruct
    public void afterPropertiesSet() {
        Assert.notNull(tenantId);
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
}
