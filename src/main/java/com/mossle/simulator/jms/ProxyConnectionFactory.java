package com.mossle.simulator.jms;

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
import javax.jms.Topic;

public class ProxyConnectionFactory implements ConnectionFactory {
    private Map<String, Destination> destinationMap = new HashMap<String, Destination>();
    private List<ProxyConnection> connections = new ArrayList<ProxyConnection>();
    private MessageHandler messageHandler = new MemoryMessageHandler();

    public Connection createConnection() throws JMSException {
        return createConnection(null, null);
    }

    public Connection createConnection(String userName, String password)
            throws JMSException {
        ProxyConnection proxyConnection = new ProxyConnection(this);
        this.connections.add(proxyConnection);

        return proxyConnection;
    }

    // ~ ==================================================
    public void removeConnection(ProxyConnection proxyConnection) {
        this.connections.remove(proxyConnection);
    }

    public void sendMessage(MessageContext messageContext,
            Destination destination, Message message) throws JMSException {
        String destinationName = destination.toString();

        if (destination instanceof Topic) {
            messageHandler.sendMessageToTopic(messageContext, destinationName,
                    message);
        } else {
            messageHandler.sendMessageToQueue(messageContext, destinationName,
                    message);
        }
    }

    public Message getMessage(MessageContext messageContext,
            ProxyMessageConsumer proxyMessageConsumer) throws JMSException {
        String destinationName = proxyMessageConsumer.getDestination()
                .toString();
        Destination destination = destinationMap.get(destinationName);

        if (destination instanceof Topic) {
            return messageHandler.consumeMessageFromTopic(messageContext,
                    destinationName, proxyMessageConsumer.getId());
        } else {
            return messageHandler.consumeMessageFromQueue(messageContext,
                    destinationName);
        }
    }

    public MessageConsumer createConsumer(Destination destination,
            ProxySession session) throws JMSException {
        String destinationName = destination.toString();
        ProxyMessageConsumer messageConsumer = new ProxyMessageConsumer(session);
        messageConsumer.setDestination(destination);

        if (destination instanceof Topic) {
            this.messageHandler.registerToTopic(destinationName,
                    messageConsumer.getId());
        }

        return messageConsumer;
    }

    public void removeMessageConsumer(ProxyMessageConsumer messageConsumer)
            throws JMSException {
        Destination destination = messageConsumer.getDestination();

        if (destination instanceof Topic) {
            String destinationName = destination.toString();
            this.messageHandler.unregisterFromTopic(destinationName,
                    messageConsumer.getId());
        }
    }

    public MessageProducer createProducer(Destination destination,
            ProxySession session) {
        ProxyMessageProducer proxyMessageProducer = new ProxyMessageProducer(
                session);
        proxyMessageProducer.setDestination(destination);

        return proxyMessageProducer;
    }

    public void onProducerConnect(MessageContext messageContext) {
        messageHandler.onProducerConnect(messageContext);
    }

    public void onConsumerConnect(MessageContext messageContext) {
        messageHandler.onConsumerConnect(messageContext);
    }

    public void onProducerDisconnect(MessageContext messageContext) {
        messageHandler.onProducerDisconnect(messageContext);
    }

    public void onConsumerDisconnect(MessageContext messageContext) {
        messageHandler.onConsumerDisconnect(messageContext);
    }
}
