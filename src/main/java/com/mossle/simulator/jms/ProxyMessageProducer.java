package com.mossle.simulator.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

public class ProxyMessageProducer implements MessageProducer {
    private ProxySession proxySession;
    private Destination defaultDestination;

    public ProxyMessageProducer(ProxySession proxySession) {
        this.proxySession = proxySession;
        this.proxySession.onProducerConnect();
    }

    public void setDisableMessageID(boolean value) throws JMSException {
    }

    public boolean getDisableMessageID() throws JMSException {
        return false;
    }

    public void setDisableMessageTimestamp(boolean value) throws JMSException {
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        return false;
    }

    public void setDeliveryMode(int deliveryMode) throws JMSException {
    }

    public int getDeliveryMode() throws JMSException {
        return 0;
    }

    public void setPriority(int defaultPriority) throws JMSException {
    }

    public int getPriority() throws JMSException {
        return 0;
    }

    public void setTimeToLive(long timeToLive) throws JMSException {
    }

    public long getTimeToLive() throws JMSException {
        return 0L;
    }

    public Destination getDestination() throws JMSException {
        return defaultDestination;
    }

    public void close() throws JMSException {
        proxySession.onProducerDisconnect();
    }

    public void send(Message message) throws JMSException {
        this.send(defaultDestination, message);
    }

    public void send(Message message, int deliveryMode, int priority,
            long timeToLive) throws JMSException {
        this.send(defaultDestination, message, 0, 0, 0L);
    }

    public void send(Destination destination, Message message)
            throws JMSException {
        this.send(destination, message, 0, 0, 0L);
    }

    public void send(Destination destination, Message message,
            int deliveryMode, int priority, long timeToLive)
            throws JMSException {
        this.proxySession.sendMessage(destination, message);
    }

    // ~ ==================================================
    public void setDestination(Destination destination) {
        this.defaultDestination = destination;
    }
}
