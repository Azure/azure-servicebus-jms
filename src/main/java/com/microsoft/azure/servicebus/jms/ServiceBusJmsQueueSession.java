// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import jakarta.jms.QueueReceiver;
import jakarta.jms.QueueSender;
import jakarta.jms.QueueSession;

class ServiceBusJmsQueueSession extends ServiceBusJmsSession implements QueueSession {
    private final QueueSession innerQueueSession;
    
    ServiceBusJmsQueueSession(QueueSession innerQueueSession) {
        super(innerQueueSession);
        this.innerQueueSession = innerQueueSession;
    }

    @Override
    public QueueReceiver createReceiver(Queue queue) throws JMSException {
        return this.innerQueueSession.createReceiver(queue);
    }

    @Override
    public QueueReceiver createReceiver(Queue queue, String messageSelector) throws JMSException {
        return this.innerQueueSession.createReceiver(queue, messageSelector);
    }

    @Override
    public QueueSender createSender(Queue queue) throws JMSException {
        return this.innerQueueSession.createSender(queue);
    }
}
