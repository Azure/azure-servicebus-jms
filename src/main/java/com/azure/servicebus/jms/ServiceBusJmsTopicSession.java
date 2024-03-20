// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Topic;
import jakarta.jms.TopicPublisher;
import jakarta.jms.TopicSession;
import jakarta.jms.TopicSubscriber;

class ServiceBusJmsTopicSession extends ServiceBusJmsSession implements TopicSession {
    private final TopicSession innerTopicSession;
    
    ServiceBusJmsTopicSession(TopicSession innerTopicSession) {
        super(innerTopicSession);
        this.innerTopicSession = innerTopicSession;
    }

    @Override
    public TopicSubscriber createSubscriber(Topic topic) throws JMSException {
        return this.innerTopicSession.createSubscriber(topic);
    }

    @Override
    public TopicSubscriber createSubscriber(Topic topic, String messageSelector, boolean noLocal) throws JMSException {
        return this.innerTopicSession.createSubscriber(topic, messageSelector, noLocal);
    }

    @Override
    public TopicPublisher createPublisher(Topic topic) throws JMSException {
        return this.innerTopicSession.createPublisher(topic);
    }
}
