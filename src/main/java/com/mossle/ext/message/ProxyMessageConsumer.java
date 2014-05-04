package com.mossle.ext.message;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyMessageConsumer implements MessageConsumer {
    private static Logger logger = LoggerFactory
            .getLogger(ProxyMessageConsumer.class);
    private ProxySession session;
    private Destination destination;
    private List<String> data = new ArrayList<String>();

    public ProxyMessageConsumer(ProxySession session) {
        this.session = session;
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
        session.removeMessageConsumer(this);
    }

    // ~ ==================================================
    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Message getMessage() {
        if (data.isEmpty()) {
            return null;
        }

        String text = data.remove(0);
        ProxyTextMessage message = new ProxyTextMessage();
        message.setText(text);

        return message;
    }

    public void sendMessage(String text) {
        data.add(text);
    }
}
