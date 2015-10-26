package com.mossle.simulator.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Topic;

public class ProxyTopic implements Topic {
    private Map<MessageConsumer, List<String>> map = new HashMap<MessageConsumer, List<String>>();
    private String name;

    public ProxyTopic(String name) {
        this.name = name;
    }

    public String getTopicName() throws JMSException {
        return name;
    }

    public String toString() {
        return name;
    }

    // ~ ==================================================
    public void sendMessage(String text) {
        for (Map.Entry<MessageConsumer, List<String>> entry : map.entrySet()) {
            entry.getValue().add(text);
        }
    }

    public void addConsumer(MessageConsumer messageConsumer) {
        map.put(messageConsumer, new ArrayList<String>());
    }

    public void removeConsumer(MessageConsumer messageConsumer) {
        map.remove(messageConsumer);
    }

    public String getMessage(MessageConsumer messageConsumer) {
        List<String> list = map.get(messageConsumer);

        if (list.isEmpty()) {
            return null;
        }

        return list.remove(0);
    }
}
