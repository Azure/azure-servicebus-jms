// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

import org.apache.qpid.jms.JmsConnectionExtensions;
import org.apache.qpid.jms.JmsConnectionFactory;

import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

public class ServiceBusJmsConnectionFactory implements ConnectionFactory, QueueConnectionFactory, TopicConnectionFactory {
    private final JmsConnectionFactory factory;
    private ConnectionStringBuilder builder;
    
    public ServiceBusJmsConnectionFactory(String connectionString, ServiceBusJmsConnectionFactorySettings settings) {
        this(new ConnectionStringBuilder(connectionString), settings);
    }
    
    public ServiceBusJmsConnectionFactory(ConnectionStringBuilder connectionStringBuilder, ServiceBusJmsConnectionFactorySettings settings) {
        this(connectionStringBuilder.getSasKeyName(),
                connectionStringBuilder.getSasKey(),
                connectionStringBuilder.getEndpoint().getHost(),
                settings);
        this.builder = connectionStringBuilder;
    }
    
    public ServiceBusJmsConnectionFactory(String sasKeyName, String sasKey, String host, ServiceBusJmsConnectionFactorySettings settings) {
        if (sasKeyName == null || sasKeyName == null || host == null) {
            throw new IllegalArgumentException("SAS Key, SAS KeyName and the host cannot be null for a ServiceBus connection factory.");
        }
        
        String query = (settings == null) ? "" : settings.toQuery();
        this.factory = new JmsConnectionFactory(sasKeyName, sasKey, "amqps://" + host + query);
        this.factory.setExtension(JmsConnectionExtensions.AMQP_OPEN_PROPERTIES.toString(), (connection, uri) -> {
            Map<String, Object> properties = new HashMap<>();
            properties.put(ServiceBusJmsConnectionFactorySettings.IsClientProvider, true);
            return properties;
        });
    }
    
    JmsConnectionFactory getConectionFactory() {
        return factory;
    }
    
    public ConnectionStringBuilder getConnectionStringBuilder() {
        return builder;
    }
    
    @Override
    public Connection createConnection() throws JMSException {
        return this.factory.createConnection();
    }

    @Override
    public Connection createConnection(String userName, String password) throws JMSException {
        return this.factory.createConnection(userName, password);
    }

    @Override
    public JMSContext createContext() {
        return this.factory.createContext();
    }

    @Override
    public JMSContext createContext(int sessionMode) {
        return this.factory.createContext(sessionMode);
    }

    @Override
    public JMSContext createContext(String userName, String password) {
        return this.factory.createContext(userName, password);
    }

    @Override
    public JMSContext createContext(String userName, String password, int sessionMode) {
        return this.factory.createContext(userName, password, sessionMode);
    }

    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        return this.factory.createTopicConnection();
    }

    @Override
    public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
        return this.factory.createTopicConnection(userName, password);
    }

    @Override
    public QueueConnection createQueueConnection() throws JMSException {
        return this.factory.createQueueConnection();
    }

    @Override
    public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
        return this.factory.createQueueConnection(userName, password);
    }
}
