// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

import java.io.Serializable;

import jakarta.jms.BytesMessage;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.MessageProducer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.QueueBrowser;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;
import jakarta.jms.TemporaryQueue;
import jakarta.jms.TemporaryTopic;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import jakarta.jms.TopicSubscriber;

class ServiceBusJmsSession implements Session {
    private final Session innerSession;
    
    ServiceBusJmsSession(Session innerSession) {
        this.innerSession = innerSession;
    }
    
    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        return this.innerSession.createBytesMessage();
    }

    @Override
    public MapMessage createMapMessage() throws JMSException {
        return this.innerSession.createMapMessage();
    }

    @Override
    public Message createMessage() throws JMSException {
        return this.innerSession.createMessage();
    }

    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        return this.innerSession.createObjectMessage();
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
        return this.innerSession.createObjectMessage(object);
    }

    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        return this.innerSession.createStreamMessage();
    }

    @Override
    public TextMessage createTextMessage() throws JMSException {
        return this.innerSession.createTextMessage();
    }

    @Override
    public TextMessage createTextMessage(String text) throws JMSException {
        return this.innerSession.createTextMessage(text);
    }

    @Override
    public boolean getTransacted() throws JMSException {
        return this.innerSession.getTransacted();
    }

    @Override
    public int getAcknowledgeMode() throws JMSException {
        return this.innerSession.getAcknowledgeMode();
    }

    @Override
    public void commit() throws JMSException {
        this.innerSession.commit();
    }

    @Override
    public void rollback() throws JMSException {
        this.innerSession.rollback();
    }

    @Override
    public void close() throws JMSException {
        this.innerSession.close();
    }

    @Override
    public void recover() throws JMSException {
        this.innerSession.recover();
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return this.innerSession.getMessageListener();
    }

    @Override
    public void setMessageListener(MessageListener listener) throws JMSException {
        this.innerSession.setMessageListener(listener);
    }

    @Override
    public void run() {
        this.innerSession.run();
    }

    @Override
    public MessageProducer createProducer(Destination destination) throws JMSException {
        return this.innerSession.createProducer(destination);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return this.innerSession.createConsumer(destination);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        return this.innerSession.createConsumer(destination, messageSelector);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean NoLocal)
            throws JMSException {
        return this.innerSession.createConsumer(destination, messageSelector, NoLocal);
    }

    @Override
    public Queue createQueue(String queueName) throws JMSException {
        Queue innerQueue = this.innerSession.createQueue(queueName);
        return new ServiceBusJmsQueue(innerQueue);
    }

    @Override
    public Topic createTopic(String topicName) throws JMSException {
        Topic innerTopic = this.innerSession.createTopic(topicName);
        return new ServiceBusJmsTopic(innerTopic);
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        return this.innerSession.createDurableSubscriber(topic, name);
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal)
            throws JMSException {
        return this.innerSession.createDurableSubscriber(topic, name, messageSelector, noLocal);
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return this.innerSession.createBrowser(queue);
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
        return this.innerSession.createBrowser(queue, messageSelector);
    }

    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return this.innerSession.createTemporaryQueue();
    }

    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return this.innerSession.createTemporaryTopic();
    }

    @Override
    public void unsubscribe(String name) throws JMSException {
        this.innerSession.unsubscribe(name);
    }

    @Override
    public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) throws JMSException {
        return this.innerSession.createSharedConsumer(topic, sharedSubscriptionName);
    }

    @Override
    public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector)
            throws JMSException {
        return this.innerSession.createSharedConsumer(topic, sharedSubscriptionName, messageSelector);
    }

    @Override
    public MessageConsumer createDurableConsumer(Topic topic, String name) throws JMSException {
        return this.innerSession.createDurableConsumer(topic, name);
    }

    @Override
    public MessageConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal)
            throws JMSException {
        return this.innerSession.createDurableConsumer(topic, name, messageSelector, noLocal);
    }

    @Override
    public MessageConsumer createSharedDurableConsumer(Topic topic, String name) throws JMSException {
        return this.innerSession.createSharedDurableConsumer(topic, name);
    }

    @Override
    public MessageConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector)
            throws JMSException {
        return this.innerSession.createSharedDurableConsumer(topic, name, messageSelector);
    }
}
