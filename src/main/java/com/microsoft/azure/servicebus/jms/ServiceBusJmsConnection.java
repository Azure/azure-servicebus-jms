// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

class ServiceBusJmsConnection implements Connection {
    private final Connection innerConnection;

    ServiceBusJmsConnection(Connection innerConnection) {
        this.innerConnection = innerConnection;
    }
    
    @Override
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        Session innerSession = this.innerConnection.createSession(transacted, acknowledgeMode);
        return new ServiceBusJmsSession(innerSession);
    }

    @Override
    public Session createSession(int sessionMode) throws JMSException {
        Session innerSession = this.innerConnection.createSession(sessionMode);
        return new ServiceBusJmsSession(innerSession);
    }

    @Override
    public Session createSession() throws JMSException {
        Session innerSession = this.innerConnection.createSession();
        return new ServiceBusJmsSession(innerSession);
    }

    @Override
    public String getClientID() throws JMSException {
        return this.innerConnection.getClientID();
    }

    @Override
    public void setClientID(String clientID) throws JMSException {
        this.innerConnection.setClientID(clientID);
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return this.innerConnection.getMetaData();
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return this.innerConnection.getExceptionListener();
    }

    @Override
    public void setExceptionListener(ExceptionListener listener) throws JMSException {
        this.innerConnection.setExceptionListener(listener);
    }

    @Override
    public void start() throws JMSException {
        this.innerConnection.start();
    }

    @Override
    public void stop() throws JMSException {
        this.innerConnection.stop();
    }

    @Override
    public void close() throws JMSException {
        this.innerConnection.close();
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector,
            ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return this.innerConnection.createConnectionConsumer(destination, messageSelector, sessionPool, maxMessages);
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName,
            String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return this.innerConnection.createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
    }

    @Override
    public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName,
            String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return this.innerConnection.createSharedDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
    }

    @Override
    public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName,
            String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return this.innerConnection.createSharedConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
    }
}
