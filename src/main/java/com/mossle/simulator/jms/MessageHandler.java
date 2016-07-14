package com.mossle.simulator.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public interface MessageHandler {
    void sendMessageToQueue(MessageContext messageContext, String queueName,
            Message message) throws JMSException;

    void sendMessageToTopic(MessageContext messageContext, String topicName,
            Message message) throws JMSException;

    Message consumeMessageFromQueue(MessageContext messageContext,
            String queueName) throws JMSException;

    Message consumeMessageFromTopic(MessageContext messageContext,
            String topicName, String consumerName) throws JMSException;

    void registerToTopic(String topicName, String consumerName)
            throws JMSException;

    void unregisterFromTopic(String topicName, String consumerName)
            throws JMSException;

    void onProducerConnect(MessageContext messageContext);

    void onConsumerConnect(MessageContext messageContext);

    void onProducerDisconnect(MessageContext messageContext);

    void onConsumerDisconnect(MessageContext messageContext);
}
