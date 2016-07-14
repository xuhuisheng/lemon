package com.mossle.simulator.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public class DefaultMessageHandler implements MessageHandler {
    public void sendMessageToQueue(MessageContext messageContext,
            String queueName, Message message) throws JMSException {
    }

    public void sendMessageToTopic(MessageContext messageContext,
            String topicName, Message message) throws JMSException {
    }

    public Message consumeMessageFromQueue(MessageContext messageContext,
            String queueName) throws JMSException {
        return null;
    }

    public Message consumeMessageFromTopic(MessageContext messageContext,
            String topicName, String consumerName) throws JMSException {
        return null;
    }

    public void registerToTopic(String topicName, String consumerName)
            throws JMSException {
    }

    public void unregisterFromTopic(String topicName, String consumerName)
            throws JMSException {
    }

    public void onProducerConnect(MessageContext messageContext) {
    }

    public void onConsumerConnect(MessageContext messageContext) {
    }

    public void onProducerDisconnect(MessageContext messageContext) {
    }

    public void onConsumerDisconnect(MessageContext messageContext) {
    }
}
