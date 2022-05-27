package com.mossle.client.svc;

public interface ServiceRegistry {
    void registerProvider(ServiceProvider serviceProvider);

    void registerConsumer(ServiceConsumer serviceConsumer);
}
