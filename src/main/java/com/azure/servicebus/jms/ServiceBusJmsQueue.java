// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.jms.JMSException;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.Queue;
import org.apache.qpid.jms.JmsQueue;

import com.azure.servicebus.jms.jndi.JNDIStorable;

/** Intended for JNDI use only. Users should not be actively referencing this class */
public final class ServiceBusJmsQueue extends JNDIStorable implements Queue {
    // JNDI property name
    static final String NAME_PROPERTY = "physicalName";
    private Queue innerQueue;
    
    /**
     * Intended to be used by JNDI only. Users should not be actively calling this constructor.
     */
    public ServiceBusJmsQueue() { }
    
    ServiceBusJmsQueue(Queue innerQueue) {
        this.innerQueue = innerQueue;
    }
    
    @Override
    public String getQueueName() throws JMSException {
        return this.innerQueue.getQueueName();
    }

    @Override
    protected Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        try {
            String name = this.getQueueName();
            properties.put(NAME_PROPERTY, name);
        } catch (JMSException exception) {
            String errorMsg = "Cannot get queue name due to exception: " + exception.getMessage();
            throw new JMSRuntimeException(errorMsg, "", exception);
        }
        
        return Collections.unmodifiableMap(properties);
    }

    @Override
    protected void setProperties(Map<String, String> properties) {
        String name = null;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(NAME_PROPERTY)) {
                name = entry.getValue();
            }
        }
        
        this.checkRequiredProperty(NAME_PROPERTY, name);
        JmsQueue innerQueue = new JmsQueue(name);
        this.innerQueue = innerQueue;
    }
}
