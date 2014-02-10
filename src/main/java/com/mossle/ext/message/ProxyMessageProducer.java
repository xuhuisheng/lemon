package com.mossle.ext.message;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

public class ProxyMessageProducer implements MessageProducer {
    private ProxySession session;
    private Destination defaultDestination;

    public ProxyMessageProducer(ProxySession session) {
        this.session = session;
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
    }

    public void send(Message message) throws JMSException {
        send(defaultDestination, message);
    }

    public void send(Message message, int deliveryMode, int priority,
            long timeToLive) throws JMSException {
        send(defaultDestination, message, 0, 0, 0L);
    }

    public void send(Destination destination, Message message)
            throws JMSException {
        send(destination, message, 0, 0, 0L);
    }

    public void send(Destination destination, Message message,
            int deliveryMode, int priority, long timeToLive)
            throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        this.sendMessage(destination, textMessage.getText());
    }

    // ~ ==================================================
    public void setDestination(Destination destination) {
        this.defaultDestination = destination;
    }

    public void sendMessage(Destination destination, String text) {
        this.session.sendMessage(destination, text);
    }
}
