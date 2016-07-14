package com.mossle.simulator.jms;

import java.io.Serializable;

import java.util.UUID;

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
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

public class ProxySession implements Session {
    private ProxyConnection proxyConnection;
    private boolean closed;
    private String id;

    public ProxySession(ProxyConnection proxyConnection) {
        this.proxyConnection = proxyConnection;
        this.id = UUID.randomUUID().toString();
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
        this.checkStatus();
    }

    public void rollback() throws JMSException {
        this.checkStatus();
    }

    public void close() throws JMSException {
        this.closed = true;
        this.proxyConnection.removeSession(this);
    }

    public void recover() throws JMSException {
        this.checkStatus();
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
        this.checkStatus();

        return this.proxyConnection.createProducer(destination, this);
    }

    public MessageConsumer createConsumer(Destination destination)
            throws JMSException {
        this.checkStatus();

        return createConsumer(destination, null, true);
    }

    public MessageConsumer createConsumer(Destination destination,
            String messageSelector) throws JMSException {
        this.checkStatus();

        return createConsumer(destination, messageSelector, true);
    }

    public MessageConsumer createConsumer(Destination destination,
            String messageSelector, boolean NoLocal) throws JMSException {
        this.checkStatus();

        return this.proxyConnection.createConsumer(destination, this);
    }

    public Queue createQueue(String queueName) throws JMSException {
        return new ProxyQueue(queueName);
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
    public void checkStatus() throws JMSException {
        if (this.closed) {
            throw new JMSException("connection " + this.id + " closed");
        }
    }

    public void sendMessage(Destination destination, Message message)
            throws JMSException {
        this.proxyConnection.sendMessage(destination, message);
    }

    public Message getMessage(ProxyMessageConsumer proxyMessageConsumer)
            throws JMSException {
        return this.proxyConnection.getMessage(proxyMessageConsumer);
    }

    public void removeMessageConsumer(ProxyMessageConsumer messageConsumer)
            throws JMSException {
        this.proxyConnection.removeMessageConsumer(messageConsumer);
    }

    public void onConsumerConnect() {
        this.proxyConnection.onConsumerConnect();
    }

    public void onProducerConnect() {
        this.proxyConnection.onProducerConnect();
    }

    public void onConsumerDisconnect() {
        this.proxyConnection.onConsumerDisconnect();
    }

    public void onProducerDisconnect() {
        this.proxyConnection.onProducerDisconnect();
    }
}
