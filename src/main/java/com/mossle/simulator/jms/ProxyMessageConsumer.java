package com.mossle.simulator.jms;

import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyMessageConsumer implements MessageConsumer {
    private static Logger logger = LoggerFactory
            .getLogger(ProxyMessageConsumer.class);
    private ProxySession proxySession;
    private Destination destination;
    private String id;

    public ProxyMessageConsumer(ProxySession proxySession) {
        this.proxySession = proxySession;
        this.id = UUID.randomUUID().toString();
        this.proxySession.onConsumerConnect();
    }

    public String getMessageSelector() throws JMSException {
        return null;
    }

    public MessageListener getMessageListener() throws JMSException {
        return null;
    }

    public void setMessageListener(MessageListener listener)
            throws JMSException {
        throw new UnsupportedOperationException("setMessageListener("
                + listener + ")");
    }

    public Message receive() throws JMSException {
        return receive(100L);
    }

    public Message receive(long timeout) throws JMSException {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            logger.warn(ex.getMessage(), ex);

            JMSException jmsException = new JMSException(ex.getMessage());
            jmsException.setLinkedException(ex);
            throw jmsException;
        }

        return getMessage();
    }

    public Message receiveNoWait() throws JMSException {
        return receive(100L);
    }

    public void close() throws JMSException {
        proxySession.removeMessageConsumer(this);
        proxySession.onConsumerDisconnect();
    }

    // ~ ==================================================
    public String getId() {
        return id;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Message getMessage() throws JMSException {
        return this.proxySession.getMessage(this);
    }
}
