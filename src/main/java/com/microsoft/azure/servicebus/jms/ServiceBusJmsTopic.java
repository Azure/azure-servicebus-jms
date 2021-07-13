// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import com.microsoft.azure.servicebus.jms.jndi.JNDIStorable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Topic;
import org.apache.qpid.jms.JmsTopic;

/** Intended for JNDI use only. Users should not be actively referencing this class */
public final class ServiceBusJmsTopic extends JNDIStorable implements Topic {
    // JNDI property name
    static final String NAME_PROPERTY = "physicalName";
    private Topic innerTopic;

    /**
     * Intended to be used by JNDI only. Users should not be actively calling this constructor.
     */
    public ServiceBusJmsTopic() { }
    
    ServiceBusJmsTopic(Topic innerTopic) {
        this.innerTopic = innerTopic;
    }
    
    @Override
    public String getTopicName() throws JMSException {
        return this.innerTopic.getTopicName();
    }

    @Override
    protected Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<String,String>();
        try {
            String name = this.getTopicName();
            properties.put(NAME_PROPERTY, name);
        } catch (JMSException exception) {
            String errorMsg = "Cannot get topic name due to exception: " + exception.getMessage();
            System.err.println(errorMsg);
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
        JmsTopic innerTopic = new JmsTopic(name);
        this.innerTopic = innerTopic;
    }
}
