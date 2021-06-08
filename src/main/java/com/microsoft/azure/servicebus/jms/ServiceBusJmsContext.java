// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;

class ServiceBusJmsContext implements JMSContext {
    private final JMSContext innerJmsContext;

    ServiceBusJmsContext(JMSContext jmsContext) {
        this.innerJmsContext = jmsContext;
    }
    
    @Override
    public void acknowledge() {
        this.innerJmsContext.acknowledge();
    }

    @Override
    public void close() {
        this.innerJmsContext.close();
    }

    @Override
    public void commit() {
        this.innerJmsContext.commit();
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) {
        return this.innerJmsContext.createBrowser(queue);
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String messageSelector) {
        return this.innerJmsContext.createBrowser(queue, messageSelector);
    }

    @Override
    public BytesMessage createBytesMessage() {
        return this.innerJmsContext.createBytesMessage();
    }

    @Override
    public JMSConsumer createConsumer(Destination destination) {
        return this.innerJmsContext.createConsumer(destination);
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String messageSelector) {
        return this.innerJmsContext.createConsumer(destination, messageSelector);
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) {
        return this.innerJmsContext.createConsumer(destination, messageSelector, noLocal);
    }

    @Override
    public JMSContext createContext(int sessionMode) {
        return this.innerJmsContext.createContext(sessionMode);
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String name) {
        return this.innerJmsContext.createDurableConsumer(topic, name);
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) {
        return this.innerJmsContext.createDurableConsumer(topic, name, messageSelector, noLocal);
    }

    @Override
    public MapMessage createMapMessage() {
        return this.innerJmsContext.createMapMessage();
    }

    @Override
    public Message createMessage() {
        return this.innerJmsContext.createMessage();
    }

    @Override
    public ObjectMessage createObjectMessage() {
        return this.innerJmsContext.createObjectMessage();
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable object) {
        return this.innerJmsContext.createObjectMessage(object);
    }

    @Override
    public JMSProducer createProducer() {
        return this.innerJmsContext.createProducer();
    }

    @Override
    public Queue createQueue(String queueName) {
        Queue innerQueue = this.innerJmsContext.createQueue(queueName);
        return new ServiceBusJmsQueue(innerQueue);
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) {
        return this.innerJmsContext.createSharedConsumer(topic, sharedSubscriptionName);
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) {
        return this.innerJmsContext.createSharedConsumer(topic, sharedSubscriptionName, messageSelector);
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String name) {
        return this.innerJmsContext.createSharedDurableConsumer(topic, name);
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) {
        return this.innerJmsContext.createSharedDurableConsumer(topic, name, messageSelector);
    }

    @Override
    public StreamMessage createStreamMessage() {
        return this.innerJmsContext.createStreamMessage();
    }

    @Override
    public TemporaryQueue createTemporaryQueue() {
        return this.innerJmsContext.createTemporaryQueue();
    }

    @Override
    public TemporaryTopic createTemporaryTopic() {
        return this.innerJmsContext.createTemporaryTopic();
    }

    @Override
    public TextMessage createTextMessage() {
        return this.innerJmsContext.createTextMessage();
    }

    @Override
    public TextMessage createTextMessage(String text) {
        return this.innerJmsContext.createTextMessage(text);
    }

    @Override
    public Topic createTopic(String topicName) {
        Topic innerTopic = this.innerJmsContext.createTopic(topicName);
        return new ServiceBusJmsTopic(innerTopic);
    }

    @Override
    public boolean getAutoStart() {
        return this.innerJmsContext.getAutoStart();
    }

    @Override
    public String getClientID() {
        return this.innerJmsContext.getClientID();
    }

    @Override
    public ExceptionListener getExceptionListener() {
        return this.innerJmsContext.getExceptionListener();
    }

    @Override
    public ConnectionMetaData getMetaData() {
        return this.innerJmsContext.getMetaData();
    }

    @Override
    public int getSessionMode() {
        return this.innerJmsContext.getSessionMode();
    }

    @Override
    public boolean getTransacted() {
        return this.innerJmsContext.getTransacted();
    }

    @Override
    public void recover() {
        this.innerJmsContext.recover();
    }

    @Override
    public void rollback() {
        this.innerJmsContext.rollback();
    }

    @Override
    public void setAutoStart(boolean autoStart) {
        this.innerJmsContext.setAutoStart(autoStart);
    }

    @Override
    public void setClientID(String clientID) {
        this.innerJmsContext.setClientID(clientID);
    }

    @Override
    public void setExceptionListener(ExceptionListener listener) {
        this.innerJmsContext.setExceptionListener(listener);
    }

    @Override
    public void start() {
        this.innerJmsContext.start();
    }

    @Override
    public void stop() {
        this.innerJmsContext.stop();
    }

    @Override
    public void unsubscribe(String name) {
        this.innerJmsContext.unsubscribe(name);
    }
}
