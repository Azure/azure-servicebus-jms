// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.azure.servicebus.jms;

import com.azure.identity.DefaultAzureCredentialBuilder;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JmsSelectorIT {
    @Test
    public void durableSubscriptionFiltersMessagesBySelector() throws Exception {
        String host = requiredEnvironment("HOST_NAMESPACE");
        String topicName = requiredEnvironment("SERVICE_BUS_JMS_TOPIC");
        ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings();
        settings.setShouldReconnect(false);
        ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory(
            new DefaultAzureCredentialBuilder().build(), host, settings);

        try (JMSContext context = factory.createContext()) {
            context.setClientID("conformance-" + UUID.randomUUID());
            Topic topic = context.createTopic(topicName);
            String subscriptionName = "selector-" + UUID.randomUUID();
            JMSConsumer consumer = context.createDurableConsumer(topic, subscriptionName, "color = 'red'", false);
            try {
                JMSProducer producer = context.createProducer();
                TextMessage rejected = context.createTextMessage("rejected");
                rejected.setStringProperty("color", "blue");
                producer.send(topic, rejected);

                TextMessage accepted = context.createTextMessage("accepted");
                accepted.setStringProperty("color", "red");
                producer.send(topic, accepted);

                Message received = consumer.receive(30_000);
                assertNotNull(received, "The subscription did not receive the matching message");
                assertEquals("accepted", ((TextMessage) received).getText());
                assertNull(consumer.receive(3_000), "The selector admitted a nonmatching message");
            } finally {
                consumer.close();
                context.unsubscribe(subscriptionName);
            }
        }
    }

    private static String requiredEnvironment(String name) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(name + " is required for the live JMS selector test");
        }
        return value;
    }
}
