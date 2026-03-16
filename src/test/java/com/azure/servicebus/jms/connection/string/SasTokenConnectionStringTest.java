// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms.connection.string;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.naming.Reference;

import com.azure.servicebus.jms.ConnectionStringBuilder;
import com.azure.servicebus.jms.ServiceBusJmsConnectionFactory;
import com.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;
import com.azure.servicebus.jms.jndi.JNDIReferenceFactory;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SAS token-based connection string support in ServiceBusJmsConnectionFactory.
 * Covers the fix for azure-servicebus-jms#58: the factory previously threw
 * IllegalArgumentException when initialized with a connection string containing
 * SharedAccessSignature instead of SharedAccessKeyName/SharedAccessKey.
 */
public class SasTokenConnectionStringTest {

    private static final String SAS_TOKEN =
            "SharedAccessSignature sr=sb%3A%2F%2Ftest-ns.servicebus.windows.net&sig=dummySig&se=1735689600&skn=TestPolicy";
    private static final String SAS_TOKEN_CONNECTION_STRING =
            "Endpoint=sb://test-ns.servicebus.windows.net/;SharedAccessSignature=" + SAS_TOKEN;
    private static final String SAS_KEY_CONNECTION_STRING =
            "Endpoint=sb://test-ns.servicebus.windows.net/;SharedAccessKeyName=TestPolicy;SharedAccessKey=dummyKey123";

    /**
     * Verifies that a connection string with SharedAccessSignature (pre-generated SAS token)
     * can be used to create a factory via the String constructor without throwing.
     * This is the primary scenario reported in issue #58.
     */
    @Test
    public void createFactoryWithSasTokenConnectionString() {
        ServiceBusJmsConnectionFactory factory = assertDoesNotThrow(() ->
                new ServiceBusJmsConnectionFactory(SAS_TOKEN_CONNECTION_STRING, null));
        assertNotNull(factory);
        assertNotNull(factory.getRemoteConnectionUri());
    }

    /**
     * Verifies that a connection string with SharedAccessSignature works via
     * the ConnectionStringBuilder constructor path.
     */
    @Test
    public void createFactoryWithSasTokenViaConnectionStringBuilder() {
        ConnectionStringBuilder builder = new ConnectionStringBuilder(SAS_TOKEN_CONNECTION_STRING);
        ServiceBusJmsConnectionFactory factory = assertDoesNotThrow(() ->
                new ServiceBusJmsConnectionFactory(builder, null));
        assertNotNull(factory);
        assertNotNull(factory.getRemoteConnectionUri());
    }

    /**
     * Verifies that the factory extracts the correct host from a SAS token connection string.
     * The remote URI should contain the host from the Endpoint property.
     */
    @Test
    public void sasTokenConnectionStringExtractsCorrectHost() {
        ServiceBusJmsConnectionFactory factory =
                new ServiceBusJmsConnectionFactory(SAS_TOKEN_CONNECTION_STRING, null);
        String remoteUri = factory.getRemoteConnectionUri();
        assertNotNull(remoteUri);
        // The URI should contain the host from the connection string
        assertTrue(remoteUri.contains("test-ns.servicebus.windows.net"),
                "Remote URI should contain the namespace host");
    }

    /**
     * Verifies that using custom settings with a SAS token connection string still works.
     */
    @Test
    public void sasTokenConnectionStringWithSettings() {
        ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings();
        settings.setShouldReconnect(false);
        ServiceBusJmsConnectionFactory factory = assertDoesNotThrow(() ->
                new ServiceBusJmsConnectionFactory(SAS_TOKEN_CONNECTION_STRING, settings));
        assertNotNull(factory);
    }

    /**
     * Regression test: Verifies that the traditional SharedAccessKeyName/SharedAccessKey
     * connection string still works after the SAS token fix.
     */
    @Test
    public void sasKeyConnectionStringStillWorks() {
        ServiceBusJmsConnectionFactory factory = assertDoesNotThrow(() ->
                new ServiceBusJmsConnectionFactory(SAS_KEY_CONNECTION_STRING, null));
        assertNotNull(factory);
        assertNotNull(factory.getRemoteConnectionUri());
    }

    /**
     * Verifies that a connection string with SharedAccessSignatureToken (alternate key name)
     * also works. The ConnectionStringBuilder accepts both 'SharedAccessSignature' and
     * 'SharedAccessSignatureToken' as valid key names.
     */
    @Test
    public void createFactoryWithSharedAccessSignatureTokenKey() {
        String connectionString =
                "Endpoint=sb://test-ns.servicebus.windows.net/;SharedAccessSignatureToken=" + SAS_TOKEN;
        ServiceBusJmsConnectionFactory factory = assertDoesNotThrow(() ->
                new ServiceBusJmsConnectionFactory(connectionString, null));
        assertNotNull(factory);
    }

    /**
     * Verifies that a connection string with neither SAS key/name nor SAS token
     * still throws IllegalArgumentException (no auth credentials at all).
     */
    @Test
    public void connectionStringWithNoAuthThrows() {
        String noAuthConnectionString = "Endpoint=sb://test-ns.servicebus.windows.net/";
        assertThrows(IllegalArgumentException.class, () ->
                new ServiceBusJmsConnectionFactory(noAuthConnectionString, null));
    }

    /**
     * Verifies the JNDI round-trip path: a factory created with a SAS token connection
     * string can be stored as a JNDI Reference and recreated via JNDIReferenceFactory.
     * The recreated factory should initialize without throwing (EP-3: setProperties path).
     */
    @Test
    public void jndiRoundTripWithSasTokenConnectionString() throws Exception {
        ServiceBusJmsConnectionFactory original =
                new ServiceBusJmsConnectionFactory(SAS_TOKEN_CONNECTION_STRING, null);
        Reference reference = original.getReference();
        assertNotNull(reference);

        JNDIReferenceFactory referenceFactory = new JNDIReferenceFactory();
        ServiceBusJmsConnectionFactory restored = (ServiceBusJmsConnectionFactory)
                referenceFactory.getObjectInstance(reference, null, null, null);
        assertNotNull(restored);
        assertNotNull(restored.getRemoteConnectionUri());
    }
}
