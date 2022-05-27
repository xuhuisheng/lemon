package com.mossle.simulator.mq;

import java.util.ArrayList;
import java.util.List;

public class MqGroup {
    private String name;
    private long offset;
    private List<String> consumers = new ArrayList<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public List<String> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<String> consumers) {
        this.consumers = consumers;
    }

    public void addConsumer(String consumer) {
        this.consumers.add(consumer);
    }

    public void removeConsumer(String consumer) {
        this.consumers.remove(consumer);
    }
}
