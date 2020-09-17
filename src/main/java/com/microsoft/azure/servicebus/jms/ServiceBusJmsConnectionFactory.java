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
    private static final int MaxCustomUserAgentLength = 128;
    private final JmsConnectionFactory factory;
    private ConnectionStringBuilder builder;
    private String customUserAgent;
    
    /**
     * Create a ServiceBusJmsConnectionFactory using a given Azure ServiceBus connection string.
     * @param connectionString The ServiceBus connection string. For details on how to get your connection string, 
     *                         please see <a href="https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-create-namespace-portal#get-the-connection-string">
     *                         https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-create-namespace-portal#get-the-connection-string</a>.
     * @param settings The options used for this ConnectionFactory. Null can be used as default.
     */
    public ServiceBusJmsConnectionFactory(String connectionString, ServiceBusJmsConnectionFactorySettings settings) {
        this(new ConnectionStringBuilder(connectionString), settings);
    }
    
    /**
     * Create a ServiceBusJmsConnectionFactory using a given Azure ServiceBus ConnectionStringBuilder.
     * @param connectionStringBuilder The ConnectionStringBuilder constructed using the SerivceBus connection string
     * @param settings The options used for this ConnectionFactory. Null can be used as default.
     */
    public ServiceBusJmsConnectionFactory(ConnectionStringBuilder connectionStringBuilder, ServiceBusJmsConnectionFactorySettings settings) {
        this(connectionStringBuilder.getSasKeyName(),
                connectionStringBuilder.getSasKey(),
                connectionStringBuilder.getEndpoint().getHost(),
                settings);
        this.builder = connectionStringBuilder;
    }
    
    /**
     * Create a ServiceBusJmsConnectionFactory using shared access key and host name.
     * @param sasKeyName The Shared access policy name.
     * @param sasKey The Shared access policy key.
     * @param host The host name of the ServiceBus namespace. Example: your-namespace-name.servicebus.windows.net
     * @param settings The options used for this ConnectionFactory. Null can be used as default.
     */
    public ServiceBusJmsConnectionFactory(String sasKeyName, String sasKey, String host, ServiceBusJmsConnectionFactorySettings settings) {
        if (sasKeyName == null || sasKeyName == null || host == null) {
            throw new IllegalArgumentException("SAS Key, SAS KeyName and the host cannot be null for a ServiceBus connection factory.");
        }
        
        if (settings == null) {
            settings = new ServiceBusJmsConnectionFactorySettings();
        }
        
        String serviceBusQuery = settings.getServiceBusQuery();
        String destinationUri = "amqps://" + host + serviceBusQuery;
        if (settings.shouldReconnect()) {
            destinationUri = getReconnectUri(destinationUri, settings);
        }
        
        this.factory = new JmsConnectionFactory(sasKeyName, sasKey, destinationUri);
        this.factory.setExtension(JmsConnectionExtensions.AMQP_OPEN_PROPERTIES.toString(), (connection, uri) -> {
            Map<String, Object> properties = new HashMap<>();
            properties.put(ServiceBusJmsConnectionFactorySettings.IsClientProvider, true);
            
            StringBuilder userAgent = new StringBuilder("ServiceBusJms");
            if (customUserAgent != null && customUserAgent.length() > 0) {
                userAgent.append("/").append(customUserAgent);
            }
            
            properties.put("user-agent", userAgent.toString());

            return properties;
        });
    }
    
    JmsConnectionFactory getConectionFactory() {
        return factory;
    }
    
    public ConnectionStringBuilder getConnectionStringBuilder() {
        return builder;
    }
    
    /**
     * @return The clientId set for this ConnectionFactory.
     */
    public String getClientId() {
        return this.factory.getClientID();
    }
    
    /**
     * @param clientId Set the clientId for this ConnectionFactory. 
     *                 Connections created with this ConnectionFactory will have this value as its clientId.
     */
    public void setClientId(String clientId) {
        this.factory.setClientID(clientId);
    }

    protected String getCustomUserAgent() {
        return customUserAgent;
    }
    
    protected void setCustomUserAgent(String customUserAgent) {
        if (customUserAgent != null && customUserAgent.length() > MaxCustomUserAgentLength) {
            throw new IllegalArgumentException("The length of the custom userAgent cannot exceed " + MaxCustomUserAgentLength);
        }
        
        this.customUserAgent = customUserAgent;
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
    
    // Obtain the reconnect URI in the form that QPID could understand.
    // Example: failover:(amqps://contoso.servicebus.windows.net?amqp.idleTimeout=30000,amqps://contoso2.servicebus.windows.net?amqp.idleTimeout=30000)?failover.maxReconnectAttempts=20
    private String getReconnectUri(String originalHost, ServiceBusJmsConnectionFactorySettings settings) {
        StringBuilder builder = new StringBuilder("failover:(");
        builder.append(originalHost);
        
        String[] reconnectHosts = settings.getReconnectHosts();
        if (settings.getReconnectHosts() != null) {
            String serviceBusQuery = settings.getServiceBusQuery();
            
            for (String reconnectHost: reconnectHosts) {
                builder.append(",");
                builder.append("amqps://");
                builder.append(reconnectHost);
                builder.append(serviceBusQuery);
            }
        }
        
        builder.append(")");
        builder.append(settings.getReconnectQuery());
        return builder.toString();
    }
}
