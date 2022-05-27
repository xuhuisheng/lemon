package com.mossle.client.mq;

public interface MqProducer {
    void send(String topic, String body);
}
