// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;

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
