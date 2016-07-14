package com.mossle.simulator.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Message;

public class MemoryMessageHandler extends DefaultMessageHandler {
    private Map<String, List<Message>> queueMessages = new HashMap<String, List<Message>>();
    private Map<String, Map<String, List<Message>>> topicMessages = new HashMap<String, Map<String, List<Message>>>();

    public void sendMessageToQueue(MessageContext messageContext, String name,
            Message message) {
        List<Message> list = queueMessages.get(name);

        if (list == null) {
            list = new ArrayList<Message>();
            queueMessages.put(name, list);
        }

        list.add(message);
    }

    public void sendMessageToTopic(MessageContext messageContext, String name,
            Message message) {
        Map<String, List<Message>> consumerMap = topicMessages.get(name);

        if (consumerMap == null) {
            return;
        }

        for (List<Message> list : consumerMap.values()) {
            if (list == null) {
                continue;
            }

            list.add(message);
        }
    }

    public Message consumeMessageFromQueue(MessageContext messageContext,
            String name) {
        List<Message> list = queueMessages.get(name);

        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        return list.remove(0);
    }

    public Message consumeMessageFromTopic(MessageContext messageContext,
            String name, String consumerName) {
        Map<String, List<Message>> consumerMap = topicMessages.get(name);

        if (consumerMap == null) {
            return null;
        }

        List<Message> list = consumerMap.get(consumerName);

        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        return list.remove(0);
    }

    public void registerToTopic(String topicName, String consumerName) {
        Map<String, List<Message>> consumerMap = topicMessages.get(topicName);

        if (consumerMap == null) {
            consumerMap = new HashMap<String, List<Message>>();
            topicMessages.put(topicName, consumerMap);
        }

        if (!consumerMap.containsKey(consumerName)) {
            consumerMap.put(consumerName, new ArrayList<Message>());
        }
    }

    public void unregisterFromTopic(String topicName, String consumerName) {
        Map<String, List<Message>> consumerMap = topicMessages.get(topicName);

        if (consumerMap == null) {
            return;
        }

        if (consumerMap.containsKey(consumerName)) {
            consumerMap.remove(consumerMap);
        }
    }
}
