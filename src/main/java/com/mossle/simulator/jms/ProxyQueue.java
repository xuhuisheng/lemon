package com.mossle.simulator.jms;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Queue;

public class ProxyQueue implements Queue {
    private List<String> list = new ArrayList<String>();
    private String name;

    public ProxyQueue(String name) {
        this.name = name;
    }

    public String getQueueName() throws JMSException {
        return name;
    }

    public String toString() {
        return name;
    }

    // ~ ==================================================
    public void sendMessage(String text) {
        list.add(text);
    }

    public String getMessage() {
        if (list.isEmpty()) {
            return null;
        }

        return list.remove(0);
    }
}
