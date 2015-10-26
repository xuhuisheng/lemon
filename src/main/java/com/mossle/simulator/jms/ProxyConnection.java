package com.mossle.simulator.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

public class ProxyConnection implements Connection {
    private ProxyConnectionFactory proxyConnectionFactory;
    private String clientId;
    private boolean active;
    private boolean closed;
    private List<ProxySession> sessions = new ArrayList<ProxySession>();
    private MessageContext messageContext = new MessageContext();

    public ProxyConnection(ProxyConnectionFactory proxyConnectionFactory) {
        this.proxyConnectionFactory = proxyConnectionFactory;
        this.clientId = UUID.randomUUID().toString();
    }

    public Session createSession(boolean transacted, int acknowledgeMode)
            throws JMSException {
        this.checkStatus();

        ProxySession proxySession = new ProxySession(this);
        this.sessions.add(proxySession);

        return proxySession;
    }

    public String getClientID() throws JMSException {
        return clientId;
    }

    public void setClientID(String clientID) throws JMSException {
        this.clientId = clientID;
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
        this.checkStatus();
        this.active = true;
    }

    public void stop() throws JMSException {
        this.checkStatus();
        this.active = false;
    }

    public void close() throws JMSException {
        this.checkStatus();
        this.closed = true;
        this.proxyConnectionFactory.removeConnection(this);
        this.onProducerDisconnect();
        this.onConsumerDisconnect();
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination,
            String messageSelector, ServerSessionPool sessionPool,
            int maxMessages) throws JMSException {
        this.checkStatus();

        return null;
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic,
            String subscriptionName, String messageSelector,
            ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        this.checkStatus();

        return null;
    }

    // ~ ==================================================
    public void checkStatus() throws JMSException {
        if (this.closed) {
            throw new JMSException("connection " + this.clientId + " closed");
        }
    }

    public void removeSession(ProxySession proxySession) {
        this.sessions.remove(proxySession);
    }

    public void sendMessage(Destination destination, Message message)
            throws JMSException {
        this.proxyConnectionFactory.sendMessage(messageContext, destination,
                message);
    }

    public Message getMessage(ProxyMessageConsumer proxyMessageConsumer)
            throws JMSException {
        return this.proxyConnectionFactory.getMessage(messageContext,
                proxyMessageConsumer);
    }

    public MessageConsumer createConsumer(Destination destination,
            ProxySession session) throws JMSException {
        return this.proxyConnectionFactory.createConsumer(destination, session);
    }

    public void removeMessageConsumer(ProxyMessageConsumer messageConsumer)
            throws JMSException {
        this.proxyConnectionFactory.removeMessageConsumer(messageConsumer);
    }

    public MessageProducer createProducer(Destination destination,
            ProxySession session) throws JMSException {
        return this.proxyConnectionFactory.createProducer(destination, session);
    }

    public void onConsumerConnect() {
        this.proxyConnectionFactory.onConsumerConnect(messageContext);
    }

    public void onProducerConnect() {
        this.proxyConnectionFactory.onProducerConnect(messageContext);
    }

    public void onConsumerDisconnect() {
        this.proxyConnectionFactory.onConsumerDisconnect(messageContext);
    }

    public void onProducerDisconnect() {
        this.proxyConnectionFactory.onProducerDisconnect(messageContext);
    }
}
