package com.mossle.ext.message;

import javax.jms.JMSException;
import javax.jms.Topic;

public class ProxyTopic implements Topic {
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
}
