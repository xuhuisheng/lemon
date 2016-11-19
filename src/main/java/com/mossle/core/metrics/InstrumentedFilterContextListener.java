package com.mossle.core.metrics;

import javax.annotation.Resource;

import com.codahale.metrics.MetricRegistry;

public class InstrumentedFilterContextListener extends
        com.codahale.metrics.servlet.InstrumentedFilterContextListener {
    private MetricRegistry metricRegistry;

    protected MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    @Resource
    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }
}
