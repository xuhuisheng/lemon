package com.mossle.core.metrics;

import javax.annotation.Resource;

import com.codahale.metrics.MetricRegistry;

public class MetricRegistryListener extends
        com.codahale.metrics.servlets.MetricsServlet.ContextListener {
    private MetricRegistry metricRegistry;

    protected MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    @Resource
    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }
}
