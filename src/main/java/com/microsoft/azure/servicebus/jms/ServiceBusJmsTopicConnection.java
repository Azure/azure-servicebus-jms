// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;

class ServiceBusJmsTopicConnection extends ServiceBusJmsConnection implements TopicConnection {
    private final TopicConnection innerTopicConnection;
    
    ServiceBusJmsTopicConnection(TopicConnection innerTopicConnection) {
        super(innerTopicConnection);
        this.innerTopicConnection = innerTopicConnection;
    }

    @Override
    public TopicSession createTopicSession(boolean transacted, int acknowledgeMode) throws JMSException {
        TopicSession innerTopicSession = this.innerTopicConnection.createTopicSession(transacted, acknowledgeMode);
        return new ServiceBusJmsTopicSession(innerTopicSession);
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Topic topic, String messageSelector,
            ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return this.innerTopicConnection.createConnectionConsumer(topic, messageSelector, sessionPool, maxMessages);
    }
}
