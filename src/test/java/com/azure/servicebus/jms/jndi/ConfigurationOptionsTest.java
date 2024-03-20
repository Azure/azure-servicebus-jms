package com.azure.servicebus.jms.jndi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.azure.servicebus.jms.ConnectionStringBuilder;
import com.azure.servicebus.jms.ServiceBusJmsConnectionFactory;
import com.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;

import jakarta.jms.Connection;

public class ConfigurationOptionsTest {
    ConnectionStringBuilder connectionStringBuilder;
    
    @BeforeEach
    public void testInitialize() {
        connectionStringBuilder = new ConnectionStringBuilder(TestUtils.TEST_CONNECTION_STRING);
    }
    
    // Test that the configurationOptions match the expected default values when and only when the configurationOptions are left unset
    @Test
    public void defaultConfigurationsTest() throws Exception {
        ServiceBusJmsConnectionFactory sbConnectionFactory = new ServiceBusJmsConnectionFactory(connectionStringBuilder, new ServiceBusJmsConnectionFactorySettings());
        Map<String, String> options = sbConnectionFactory.getSettings().getConfigurationOptions();
        assertEquals("0", options.get("jms.prefetchPolicy.all"));
        try(Connection connection = sbConnectionFactory.createConnection()) {
            connection.start();
            connection.createSession();
        }
        
        Map<String, String> expectedOptions = new HashMap<String, String>();
        expectedOptions.put("jms.prefetchPolicy.all", "100");
        ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings(expectedOptions);
        sbConnectionFactory = new ServiceBusJmsConnectionFactory(connectionStringBuilder, settings);
        Map<String, String> actualOptions = sbConnectionFactory.getSettings().getConfigurationOptions();
        assertEquals("100", actualOptions.get("jms.prefetchPolicy.all"));
        try(Connection connection = sbConnectionFactory.createConnection()) {
            connection.start();
            connection.createSession();
        }
    }
    
    // Test that ConnectionFactory runs when the ServiceBusJmsConnectionFactorySettings are null
    @Test
    public void nullConfigurationsTest() throws Exception {
        ServiceBusJmsConnectionFactory sbConnectionFactory = new ServiceBusJmsConnectionFactory(connectionStringBuilder, null);
        try(Connection connection = sbConnectionFactory.createConnection()) {
            connection.start();
            connection.createSession();
        }
    }
}
