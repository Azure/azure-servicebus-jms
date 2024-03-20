// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms.jndi;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import javax.naming.NamingException;
import javax.naming.Reference;

class TestUtils {
    static final String TEST_CONNECTION_STRING = System.getenv("SERVICE_BUS_CONNECTION_STRING");

    static void testInvalidJNDIStorable(JNDIStorable invalidStorable, String propertyName) throws Exception {
        boolean caughtExpectedException = false;
        try {
            Reference reference = invalidStorable.getReference();
            JNDIReferenceFactory referenceFactory = new JNDIReferenceFactory();
            referenceFactory.getObjectInstance(reference, null, null, null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(
                    String.format(JNDIStorable.REQUIRED_PROPERTY_MESSAGE_FORMAT, invalidStorable.getClass().getName(), propertyName)));
            caughtExpectedException = true;
        } catch (NamingException e) {
            assertTrue(e.getMessage().contains(
                    String.format(JNDIStorable.REQUIRED_PROPERTY_MESSAGE_FORMAT, invalidStorable.getClass().getName(), propertyName)));
            caughtExpectedException = true;
        }
        
        if (!caughtExpectedException) {
            fail("Should have failed with invalid name on the " + invalidStorable.getClass().getName());
        }
    }
}
