package com.mossle.simulator.mq;

import com.mossle.client.mq.MqProducer;

public class MemoryMqProducer implements MqProducer {
    public void send(String topic, String body) {
        MqBroker.getInstance().send(topic, body);
    }
}
