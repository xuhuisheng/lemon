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
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

public class ProxyConnectionFactory implements ConnectionFactory {
    private Map<String, Destination> destinationMap = new HashMap<String, Destination>();
    private ProxyConnection connection;

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
            // sendTopic(destinationName, text);
            ProxyTopic proxyTopic = (ProxyTopic) destinationMap
                    .get(destinationName);

            if (proxyTopic != null) {
                proxyTopic.sendMessage(text);
            }
        } else {
            // sendQueue(destinationName, text);
            ProxyQueue proxyQueue = (ProxyQueue) destinationMap
                    .get(destinationName);

            if (proxyQueue == null) {
                proxyQueue = new ProxyQueue(destinationName);
                destinationMap.put(destinationName, proxyQueue);
            }

            proxyQueue.sendMessage(text);
        }
    }

    public Message getMessage(ProxyMessageConsumer proxyMessageConsumer) {
        String destinationName = proxyMessageConsumer.getDestination()
                .toString();
        Destination destination = destinationMap.get(destinationName);

        if (destination instanceof Topic) {
            ProxyTopic proxyTopic = (ProxyTopic) destinationMap
                    .get(destinationName);

            String text = proxyTopic.getMessage(proxyMessageConsumer);

            if (text == null) {
                return null;
            }

            ProxyTextMessage message = new ProxyTextMessage();
            message.setText(text);

            return message;
        } else {
            ProxyQueue proxyQueue = (ProxyQueue) destinationMap
                    .get(destinationName);

            if (proxyQueue == null) {
                return null;
            }

            String text = proxyQueue.getMessage();

            if (text == null) {
                return null;
            }

            ProxyTextMessage message = new ProxyTextMessage();
            message.setText(text);

            return message;
        }
    }

    public MessageConsumer createConsumer(Destination destination,
            ProxySession session) {
        String destinationName = destination.toString();
        ProxyMessageConsumer messageConsumer = new ProxyMessageConsumer(session);
        messageConsumer.setDestination(destination);

        if (destination instanceof Topic) {
            ProxyTopic proxyTopic = (ProxyTopic) destinationMap
                    .get(destinationName);

            if (proxyTopic == null) {
                proxyTopic = new ProxyTopic(destinationName);
                destinationMap.put(destinationName, proxyTopic);
            }

            proxyTopic.addConsumer(messageConsumer);
        } else {
            ProxyQueue proxyQueue = (ProxyQueue) destinationMap
                    .get(destinationName);

            if (proxyQueue == null) {
                proxyQueue = new ProxyQueue(destinationName);
                destinationMap.put(destinationName, proxyQueue);
            }
        }

        return messageConsumer;
    }

    public void removeMessageConsumer(ProxyMessageConsumer messageConsumer) {
        Destination destination = messageConsumer.getDestination();
        String destinationName = destination.toString();
        ProxyTopic proxyTopic = (ProxyTopic) destinationMap
                .get(destinationName);

        if (proxyTopic == null) {
            return;
        }

        proxyTopic.removeConsumer(messageConsumer);
    }

    public MessageProducer createProducer(Destination destination,
            ProxySession session) {
        ProxyMessageProducer proxyMessageProducer = new ProxyMessageProducer(
                session);
        proxyMessageProducer.setDestination(destination);

        return proxyMessageProducer;
    }
}
