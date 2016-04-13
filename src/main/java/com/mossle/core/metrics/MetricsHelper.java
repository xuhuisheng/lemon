package com.mossle.core.metrics;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

public class MetricsHelper {
    private MetricRegistry metricRegistry;
    private HealthCheckRegistry healthCheckRegistry;
    private ConsoleReporter consoleReporter;

    /*
     * public static void main(String args[]) { startReport(); Meter requests = metrics.meter("requests");
     * requests.mark(); wait5Seconds(); }
     */
    @PostConstruct
    public void init() {
        /*
         * consoleReporter = ConsoleReporter.forRegistry(metricRegistry) .convertRatesTo(TimeUnit.SECONDS)
         * .convertDurationsTo(TimeUnit.MILLISECONDS) .build(); consoleReporter.start(1, TimeUnit.SECONDS);
         */
        GarbageCollectorMetricSet gc = new GarbageCollectorMetricSet();

        // FileDescriptorRatioGauge fd = new FileDescriptorRatioGauge();
        MemoryUsageGaugeSet mu = new MemoryUsageGaugeSet();

        // ThreadDeadlockDetector td = new ThreadDeadlockDetector();

        // ThreadDump t = new ThreadDump();
        ThreadStatesGaugeSet ts = new ThreadStatesGaugeSet();

        metricRegistry.register("GarbageCollector", gc);
        // registry.register(FileDescriptorRatioGauge.class.getName(), fd);
        metricRegistry.register("MemoryUsage", mu);
        // registry.register(ThreadDeadlockDetector.class.getName(), td);
        // registry.registerAll(t);
        metricRegistry.register("ThreadStates", ts);
        healthCheckRegistry.register("threadDeadlock",
                new ThreadDeadlockHealthCheck());
    }

    @PreDestroy
    public void close() {
        /*
         * consoleReporter.stop();
         */
    }

    @Resource
    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Resource
    public void setHealthCheckRegistry(HealthCheckRegistry healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }
}
