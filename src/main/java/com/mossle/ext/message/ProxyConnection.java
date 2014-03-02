package com.mossle.ext.message;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

public class ProxyConnection implements Connection {
    private ProxyConnectionFactory connectionFactory;
    private ProxySession proxySession;

    public ProxyConnection(ProxyConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        proxySession = new ProxySession(this);
    }

    public Session createSession(boolean transacted, int acknowledgeMode)
            throws JMSException {
        return proxySession;
    }

    public String getClientID() throws JMSException {
        return null;
    }

    public void setClientID(String clientID) throws JMSException {
    }

    public ConnectionMetaData getMetaData() throws JMSException {
        return null;
    }

    public ExceptionListener getExceptionListener() throws JMSException {
        return null;
    }

    public void setExceptionListener(ExceptionListener listener)
            throws JMSException {
    }

    public void start() throws JMSException {
    }

    public void stop() throws JMSException {
    }

    public void close() throws JMSException {
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination,
            String messageSelector, ServerSessionPool sessionPool,
            int maxMessages) throws JMSException {
        return null;
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic,
            String subscriptionName, String messageSelector,
            ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return null;
    }

    // ~ ==================================================
    public void sendMessage(Destination destination, String text) {
        this.connectionFactory.sendMessage(destination, text);
    }

    public Message getMessage(String destinationName) {
        return connectionFactory.getMessage(destinationName);
    }

    public MessageConsumer createConsumer(Destination destination,
            ProxySession session) {
        return connectionFactory.createConsumer(destination, session);
    }

    public void removeMessageConsumer(ProxyMessageConsumer messageConsumer) {
        connectionFactory.removeMessageConsumer(messageConsumer);
    }
}
