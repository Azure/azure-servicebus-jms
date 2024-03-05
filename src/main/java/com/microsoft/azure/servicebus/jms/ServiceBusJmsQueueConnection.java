// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import jakarta.jms.ConnectionConsumer;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueSession;
import jakarta.jms.ServerSessionPool;

class ServiceBusJmsQueueConnection extends ServiceBusJmsConnection implements QueueConnection {
    private final QueueConnection innerQueueConnection;

    ServiceBusJmsQueueConnection(QueueConnection innerQueueConnection) {
        super(innerQueueConnection);
        this.innerQueueConnection = innerQueueConnection;
    }

    @Override
    public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        QueueSession innerQueueSession = this.innerQueueConnection.createQueueSession(transacted, acknowledgeMode);
        return new ServiceBusJmsQueueSession(innerQueueSession);
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Queue queue, String messageSelector,
            ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return this.innerQueueConnection.createConnectionConsumer(queue, messageSelector, sessionPool, maxMessages);
    }
}
