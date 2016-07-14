package com.mossle.core.metrics;

import javax.annotation.Resource;

import com.codahale.metrics.health.HealthCheckRegistry;

public class HealthCheckRegistryListener extends
        com.codahale.metrics.servlets.HealthCheckServlet.ContextListener {
    private HealthCheckRegistry healthCheckRegistry;

    protected HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    @Resource
    public void setHealthCheckRegistry(HealthCheckRegistry healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }
}
