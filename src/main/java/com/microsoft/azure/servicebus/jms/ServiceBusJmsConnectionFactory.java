// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

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

import com.microsoft.azure.servicebus.jms.jndi.JNDIStorable;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

import io.netty.handler.proxy.ProxyHandler;
import io.netty.util.internal.StringUtil;

public class ServiceBusJmsConnectionFactory extends JNDIStorable implements ConnectionFactory, QueueConnectionFactory, TopicConnectionFactory {
    // JNDI property names
    private static final String CONNECTION_STRING_PROPERTY = "connectionString";
    private static final String CLIENT_ID_PROPERTY = "clientId";
    
    private static final int MaxCustomUserAgentLength = 128;
    private final ServiceBusJmsConnectionFactorySettings settings;
    private volatile boolean initialized;
    private JmsConnectionFactory factory;
    private ConnectionStringBuilder builder;
    private String customUserAgent;
    
    /**
     * Intended to be used by JNDI only. Users should not be actively calling this constructor to create a ServiceBusJmsConnectionFactory instance.
     */
    public ServiceBusJmsConnectionFactory() { 
        this.settings = null;
    }
    
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
        this.builder = connectionStringBuilder;
        this.settings = settings;
        this.initialize(connectionStringBuilder.getSasKeyName(),
                connectionStringBuilder.getSasKey(),
                connectionStringBuilder.getEndpoint().getHost(),
                settings);
    }
    
    /**
     * Create a ServiceBusJmsConnectionFactory using shared access key and host name.
     * @param sasKeyName The Shared access policy name.
     * @param sasKey The Shared access policy key.
     * @param host The host name of the ServiceBus namespace. Example: your-namespace-name.servicebus.windows.net
     * @param settings The options used for this ConnectionFactory. Null can be used as default.
     */
    public ServiceBusJmsConnectionFactory(String sasKeyName, String sasKey, String host, ServiceBusJmsConnectionFactorySettings settings) {
        this.settings = settings;
        this.initialize(sasKeyName, sasKey, host, settings);
    }
    
    private void initialize(String sasKeyName, String sasKey, String host, ServiceBusJmsConnectionFactorySettings settings) {
        if (sasKeyName == null || sasKeyName == null || host == null) {
            throw new IllegalArgumentException("SAS Key, SAS KeyName and the host cannot be null for a ServiceBus connection factory.");
        }
        
        if (settings == null) {
            settings = new ServiceBusJmsConnectionFactorySettings();
        }
        
        if (this.builder == null) {
            try {
                this.builder = new ConnectionStringBuilder(new URI(host), null, sasKeyName, sasKey);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        
        String destinationUri = "amqps://" + host;
        if (settings.shouldReconnect()) {
            destinationUri = getReconnectUri(destinationUri, settings);
        }
        
        String serviceBusQuery = settings.getServiceBusQuery();
        destinationUri += serviceBusQuery;
        this.factory = new JmsConnectionFactory(sasKeyName, sasKey, destinationUri);
        this.factory.setExtension(JmsConnectionExtensions.AMQP_OPEN_PROPERTIES.toString(), (connection, uri) -> {
            Map<String, Object> properties = new HashMap<>();
            properties.put(ServiceBusJmsConnectionFactorySettings.IsClientProvider, true);
            
            String servicebusJmsVersion = "";
            Properties applicationProperties = new Properties();
            try {
                applicationProperties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
                servicebusJmsVersion = applicationProperties.getProperty("azure.servicebus.jms.version");
            } catch (IOException e) {
                servicebusJmsVersion = "unknown";
            }
            
            StringBuilder userAgent = new StringBuilder("ServiceBusJms");
            userAgent.append("-").append(servicebusJmsVersion);
            if (customUserAgent != null && customUserAgent.length() > 0) {
                userAgent.append("/").append(customUserAgent);
            }
            
            properties.put("user-agent", userAgent.toString());

            return properties;
        });

        Supplier<ProxyHandler> proxyHandlerSupplier = settings.getProxyHandlerSupplier();
        if (proxyHandlerSupplier != null) {
            factory.setExtension(JmsConnectionExtensions.PROXY_HANDLER_SUPPLIER.toString(), (connection, remote) -> {
                return proxyHandlerSupplier;
            });
        }
        
        this.initialized = true;
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
    
    /**
     * @return The ServiceBusConnectionFactorySettings used to initialize this ServiceBusJmsConnectionFactory
     */
    public ServiceBusJmsConnectionFactorySettings getSettings() {
        return this.settings;
    }
    
    @Override
    public Connection createConnection() throws JMSException {
        this.ensureInitialized();
        Connection innerConnection = this.factory.createConnection();
        return new ServiceBusJmsConnection(innerConnection);
    }

    @Override
    public Connection createConnection(String userName, String password) throws JMSException {
        this.ensureInitialized();
        Connection innerConnection = this.factory.createConnection(userName, password);
        return new ServiceBusJmsConnection(innerConnection);
    }

    @Override
    public JMSContext createContext() {
        this.ensureInitialized();
        JMSContext innerContext = this.factory.createContext();
        return new ServiceBusJmsContext(innerContext);
    }

    @Override
    public JMSContext createContext(int sessionMode) {
        this.ensureInitialized();
        JMSContext innerContext = this.factory.createContext(sessionMode);
        return new ServiceBusJmsContext(innerContext);
    }

    @Override
    public JMSContext createContext(String userName, String password) {
        this.ensureInitialized();
        JMSContext innerContext = this.factory.createContext(userName, password);
        return new ServiceBusJmsContext(innerContext);
    }

    @Override
    public JMSContext createContext(String userName, String password, int sessionMode) {
        this.ensureInitialized();
        JMSContext innerContext = this.factory.createContext(userName, password, sessionMode);
        return new ServiceBusJmsContext(innerContext);
    }

    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        this.ensureInitialized();
        TopicConnection innerTopicConnection = this.factory.createTopicConnection();
        return new ServiceBusJmsTopicConnection(innerTopicConnection);
    }

    @Override
    public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
        this.ensureInitialized();
        TopicConnection innerTopicConnection = this.factory.createTopicConnection(userName, password);
        return new ServiceBusJmsTopicConnection(innerTopicConnection);
    }

    @Override
    public QueueConnection createQueueConnection() throws JMSException {
        this.ensureInitialized();
        QueueConnection innerQueueConnection = this.factory.createQueueConnection();
        return new ServiceBusJmsQueueConnection(innerQueueConnection);
    }

    @Override
    public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
        this.ensureInitialized();
        QueueConnection innerQueueConnection = this.factory.createQueueConnection(userName, password);
        return new ServiceBusJmsQueueConnection(innerQueueConnection);
    }
    
    @Override
    protected Map<String, String> getProperties() {
        // build a map of properties for JNDI
        Map<String, String> properties = new HashMap<>();
        
        String connectionString = this.builder == null ? null : this.builder.toString();
        properties.put(CONNECTION_STRING_PROPERTY, connectionString);

        if (this.factory != null && StringUtil.isNullOrEmpty(this.getClientId())) {
            properties.put(CLIENT_ID_PROPERTY, this.getClientId());
        }
        
        return Collections.unmodifiableMap(properties);
    }
    
    @Override
    protected void setProperties(Map<String, String> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("The given properties should not be null.");
        }
        
        // TODO: support JNDI for the various configurations of ServiceBusJmsConnectionFactorySettings
        String connectionString = null;
        String clientId = null;
        for (Map.Entry<String,String> property : properties.entrySet()) {
            String propertyName = property.getKey();
            if (propertyName != null) {
                if (propertyName.equalsIgnoreCase(CONNECTION_STRING_PROPERTY)) {
                    connectionString = property.getValue();
                }
                
                if (propertyName.equalsIgnoreCase(CLIENT_ID_PROPERTY)) {
                    clientId = property.getValue();
                }
            }
        }
        
        this.checkRequiredProperty(CONNECTION_STRING_PROPERTY, connectionString);
        this.builder = new ConnectionStringBuilder(connectionString);
        this.initialize(this.builder.getSasKeyName(), this.builder.getSasKey(), this.builder.getEndpoint().getHost(), null);
    
        // Need to wait until the inner factory is initialized in order to set the clientId
        if (!StringUtil.isNullOrEmpty(clientId)) {
            // QPID does not allow setting null or empty clientId
            this.setClientId(clientId);
        }
    }
    
    ConnectionFactory getConectionFactory() {
        return factory;
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
    
    private void ensureInitialized() {
        if (!this.initialized) {
            throw new RuntimeException("This ServiceBusJmsConnectionFactory object is not initialized with the proper parameters.");
        }
    }
}
