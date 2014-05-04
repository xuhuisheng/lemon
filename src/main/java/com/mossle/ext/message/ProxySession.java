package com.mossle.ext.message;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

public class ProxySession implements Session {
    private ProxyConnection connection;
    private ProxyMessageProducer proxyMessageProducer;
    private ProxyMessageConsumer proxyMessageConsumer;

    public ProxySession(ProxyConnection connection) {
        this.connection = connection;
        proxyMessageProducer = new ProxyMessageProducer(this);
        proxyMessageConsumer = new ProxyMessageConsumer(this);
    }

    public BytesMessage createBytesMessage() throws JMSException {
        return null;
    }

    public MapMessage createMapMessage() throws JMSException {
        return null;
    }

    public Message createMessage() throws JMSException {
        return null;
    }

    public ObjectMessage createObjectMessage() throws JMSException {
        return null;
    }

    public ObjectMessage createObjectMessage(Serializable object)
            throws JMSException {
        return null;
    }

    public StreamMessage createStreamMessage() throws JMSException {
        return null;
    }

    public TextMessage createTextMessage() throws JMSException {
        return new ProxyTextMessage();
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        TextMessage textMessage = new ProxyTextMessage();
        textMessage.setText(text);

        return textMessage;
    }

    public boolean getTransacted() throws JMSException {
        return false;
    }

    public int getAcknowledgeMode() throws JMSException {
        return 0;
    }

    public void commit() throws JMSException {
    }

    public void rollback() throws JMSException {
    }

    public void close() throws JMSException {
    }

    public void recover() throws JMSException {
    }

    public MessageListener getMessageListener() throws JMSException {
        return null;
    }

    public void setMessageListener(MessageListener listener)
            throws JMSException {
    }

    public void run() {
    }

    public MessageProducer createProducer(Destination destination)
            throws JMSException {
        proxyMessageProducer.setDestination(destination);

        return proxyMessageProducer;
    }

    public MessageConsumer createConsumer(Destination destination)
            throws JMSException {
        return createConsumer(destination, null, true);
    }

    public MessageConsumer createConsumer(Destination destination,
            String messageSelector) throws JMSException {
        return createConsumer(destination, messageSelector, true);
    }

    public MessageConsumer createConsumer(Destination destination,
            String messageSelector, boolean NoLocal) throws JMSException {
        return connection.createConsumer(destination, this);
    }

    public Queue createQueue(String queueName) throws JMSException {
        throw new UnsupportedOperationException("createQueue(" + queueName
                + ")");
    }

    public Topic createTopic(String topicName) throws JMSException {
        return new ProxyTopic(topicName);
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name)
            throws JMSException {
        return null;
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name,
            String messageSelector, boolean noLocal) throws JMSException {
        return null;
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return null;
    }

    public QueueBrowser createBrowser(Queue queue, String messageSelector)
            throws JMSException {
        return null;
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return null;
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return null;
    }

    public void unsubscribe(String name) throws JMSException {
    }

    // ~ ==================================================
    public void sendMessage(Destination destination, String text) {
        this.connection.sendMessage(destination, text);
    }

    public Message getMessage(String destinationName) {
        return connection.getMessage(destinationName);
    }

    public void removeMessageConsumer(ProxyMessageConsumer messageConsumer) {
        connection.removeMessageConsumer(messageConsumer);
    }
}
