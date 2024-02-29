// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms.jndi;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.jms.*;
import javax.naming.Reference;
import org.junit.jupiter.api.Test;

import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactory;
import com.microsoft.azure.servicebus.jms.ConnectionStringBuilder;

public class JNDIConnectionFactoryTests {
    
    @Test
    public void storeAndCreateConnectionFactoryTest() throws Exception {
        ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(TestUtils.TEST_CONNECTION_STRING);
        ServiceBusJmsConnectionFactory sbConnectionFactory = new ServiceBusJmsConnectionFactory(connectionStringBuilder, null);
        Reference reference = sbConnectionFactory.getReference();
        assertNotNull(reference);
        
        JNDIReferenceFactory referenceFactory = new JNDIReferenceFactory();
        sbConnectionFactory = (ServiceBusJmsConnectionFactory)referenceFactory.getObjectInstance(reference, null, null, null);
        
        // Test to see if the ServiceBusJmsConnectionFactory is usable
        try (Connection connection = sbConnectionFactory.createConnection()) {
            try (Session session = connection.createSession()) {
                session.createTemporaryQueue();
            }
        }
    }
    
    // 'connectionString' is a mandatory field, users should not be able to do JNDI without setting it on the connection factory.
    @Test
    public void emptyConnectionStringTest() throws Exception {
        ServiceBusJmsConnectionFactory cf = new ServiceBusJmsConnectionFactory();
        TestUtils.testInvalidJNDIStorable(cf, "connectionString");
    }
}
