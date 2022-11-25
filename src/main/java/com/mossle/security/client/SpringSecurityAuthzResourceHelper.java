package com.mossle.security.client;

import javax.annotation.Resource;

import com.mossle.client.authz.AuthzResourceHelper;

public class SpringSecurityAuthzResourceHelper implements AuthzResourceHelper {
    private ResourceDetailsMonitor resourceDetailsMonitor;

    public void refresh() {
        resourceDetailsMonitor.refresh();
    }

    @Resource
    public void setResourceDetailsMonitor(
            ResourceDetailsMonitor resourceDetailsMonitor) {
        this.resourceDetailsMonitor = resourceDetailsMonitor;
    }
}
