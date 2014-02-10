package com.mossle.ext.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

public class ProxyConnectionFactory implements ConnectionFactory {
    private Map<String, List<String>> data = new HashMap<String, List<String>>();
    private ProxyConnection connection;
    private Map<String, List<ProxyMessageConsumer>> messageConsumerMap = new HashMap<String, List<ProxyMessageConsumer>>();

    public ProxyConnectionFactory() {
        connection = new ProxyConnection(this);
    }

    public Connection createConnection() throws JMSException {
        return createConnection(null, null);
    }

    public Connection createConnection(String userName, String password)
            throws JMSException {
        return connection;
    }

    // ~ ==================================================
    public void sendMessage(Destination destination, String text) {
        String destinationName = destination.toString();

        if (destination instanceof Topic) {
            sendTopic(destinationName, text);
        } else {
            sendQueue(destinationName, text);
        }
    }

    public void sendTopic(String destinationName, String text) {
        List<ProxyMessageConsumer> messageConsumers = messageConsumerMap
                .get(destinationName);

        if (messageConsumers == null) {
            return;
        }

        for (ProxyMessageConsumer messageConsumer : messageConsumers) {
            messageConsumer.sendMessage(text);
        }
    }

    public void sendQueue(String destinationName, String text) {
        // send to queue
        List<String> list = this.data.get(destinationName);

        if (list == null) {
            list = new ArrayList<String>();
            this.data.put(destinationName, list);
        }

        list.add(text);
    }

    public Message getMessage(String destinationName) {
        List<String> list = data.get(destinationName);

        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        String text = list.remove(0);
        ProxyTextMessage message = new ProxyTextMessage();
        message.setText(text);

        return message;
    }

    public MessageConsumer createConsumer(Destination destination,
            ProxySession session) {
        String destinationName = destination.toString();
        List<ProxyMessageConsumer> messageConsumers = messageConsumerMap
                .get(destinationName);

        if (messageConsumers == null) {
            messageConsumers = new ArrayList<ProxyMessageConsumer>();
            messageConsumerMap.put(destinationName, messageConsumers);
        }

        ProxyMessageConsumer messageConsumer = new ProxyMessageConsumer(session);
        messageConsumer.setDestination(destination);
        messageConsumers.add(messageConsumer);

        return messageConsumer;
    }

    public void removeMessageConsumer(ProxyMessageConsumer messageConsumer) {
        Destination destination = messageConsumer.getDestination();
        String destinationName = destination.toString();
        List<ProxyMessageConsumer> messageConsumers = messageConsumerMap
                .get(destinationName);

        if (messageConsumers == null) {
            return;
        }

        messageConsumers.remove(messageConsumer);
    }
}
