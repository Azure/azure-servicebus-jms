// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import jakarta.jms.JMSException;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicConnectionFactory;

public class ServiceBusJmsTopicConnectionFactory extends ServiceBusJmsConnectionFactory implements TopicConnectionFactory {

    public ServiceBusJmsTopicConnectionFactory(String connectionString, ServiceBusJmsConnectionFactorySettings settings) {
        super(connectionString, settings);
    }
    
    public ServiceBusJmsTopicConnectionFactory(ConnectionStringBuilder connectionStringBuilder, ServiceBusJmsConnectionFactorySettings settings) {
        super(connectionStringBuilder, settings);
    }
    
    public ServiceBusJmsTopicConnectionFactory(String sasKeyName, String sasKey, String host, ServiceBusJmsConnectionFactorySettings settings) {
        super(sasKeyName, sasKey, host, settings);
    }

    public TopicConnection createTopicConnection() throws JMSException {
        TopicConnection innerTopicConnection = super.createTopicConnection();
        return new ServiceBusJmsTopicConnection(innerTopicConnection);
    }

    public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
        TopicConnection innerTopicConnection = super.createTopicConnection(userName, password);
        return new ServiceBusJmsTopicConnection(innerTopicConnection);
    }
}
