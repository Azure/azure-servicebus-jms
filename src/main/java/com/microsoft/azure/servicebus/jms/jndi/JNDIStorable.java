// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms.jndi;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

import io.netty.util.internal.StringUtil;

public abstract class JNDIStorable implements Referenceable {
    protected static final String REQUIRED_PROPERTY_MESSAGE_FORMAT = "An instance of %s must have a valid value for its '%s' property.";
    
    @Override
    public Reference getReference() throws NamingException {
        return JNDIReferenceFactory.createReference(this.getClass().getName(), this);
    }
    
    protected abstract Map<String, String> getProperties();
    
    protected abstract void setProperties(Map<String, String> properties);
    
    /**
     * Ensure that the required property's value is not null or empty
     */
    protected void checkRequiredProperty(String propertyName, String propertyValue) {
        if (StringUtil.isNullOrEmpty(propertyValue)) {
            throw new IllegalArgumentException(
                    String.format(REQUIRED_PROPERTY_MESSAGE_FORMAT, this.getClass().getName(), propertyName));
        }
    }
}
