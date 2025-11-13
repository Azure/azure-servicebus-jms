// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.jms.Connection;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import jakarta.jms.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.azure.servicebus.jms.jndi.TestUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServiceBusJmsTopicEqualsHashCodeTest {
    private ServiceBusJmsConnectionFactory connectionFactory;
    private static final String TOPIC_NAME = "test-topic";
    private static final String DIFFERENT_TOPIC_NAME = "different-topic";

    @BeforeEach
    public void setUp() {
        ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(TestUtils.TEST_CONNECTION_STRING);
        connectionFactory = new ServiceBusJmsConnectionFactory(connectionStringBuilder, null);
    }

    @Test
    public void testTopicEquality() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            // Create two topics with the same name
            Topic topic1 = session.createTopic(TOPIC_NAME);
            Topic topic2 = session.createTopic(TOPIC_NAME);
            
            // They should be equal
            assertEquals(topic1, topic2);
            assertEquals(topic2, topic1);
            
            // Reflexivity
            assertEquals(topic1, topic1);
            
            // Create a topic with different name
            Topic differentTopic = session.createTopic(DIFFERENT_TOPIC_NAME);
            
            // They should not be equal
            assertNotEquals(topic1, differentTopic);
            assertNotEquals(differentTopic, topic1);
        }
    }

    @Test
    public void testTopicHashCode() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            // Create two topics with the same name
            Topic topic1 = session.createTopic(TOPIC_NAME);
            Topic topic2 = session.createTopic(TOPIC_NAME);
            
            // They should have the same hash code
            assertEquals(topic1.hashCode(), topic2.hashCode());
            
            // Create a topic with different name
            Topic differentTopic = session.createTopic(DIFFERENT_TOPIC_NAME);
            
            // Different topics may have different hash codes (not required, but likely)
            // We don't assert inequality here as hash collisions are allowed
        }
    }

    @Test
    public void testTopicToString() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            Topic topic = session.createTopic(TOPIC_NAME);
            String toString = topic.toString();
            
            // toString should contain the topic name
            assertTrue(toString.contains(TOPIC_NAME));
            assertTrue(toString.contains("ServiceBusJmsTopic"));
            
            // toString should be consistent
            assertEquals(toString, topic.toString());
        }
    }

    @Test
    public void testNullEquality() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            Topic topic = session.createTopic(TOPIC_NAME);
            
            // Topic should not equal null
            assertNotEquals(topic, null);
            assertNotEquals(null, topic);
        }
    }

    @Test
    public void testDifferentClassEquality() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            Topic topic = session.createTopic(TOPIC_NAME);
            String string = "not a topic";
            
            // Topic should not equal object of different class
            assertNotEquals(topic, string);
        }
    }

    @Test
    public void testSpringCachingSimulation() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            // Simulate Spring's CachingConnectionFactory behavior
            Map<Topic, String> producerCache = new HashMap<>();
            
            // Create multiple topic instances with same name
            Topic topic1 = session.createTopic(TOPIC_NAME);
            Topic topic2 = session.createTopic(TOPIC_NAME);
            Topic topic3 = session.createTopic(TOPIC_NAME);
            
            // Simulate caching producers
            producerCache.put(topic1, "producer1");
            
            // All instances should retrieve the same cached producer
            assertEquals("producer1", producerCache.get(topic1));
            assertEquals("producer1", producerCache.get(topic2));
            assertEquals("producer1", producerCache.get(topic3));
            
            // Cache should only contain one entry
            assertEquals(1, producerCache.size());
        }
    }

    @Test
    public void testQueueTopicInequality() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            // Create a topic and queue with the same name
            Topic topic = session.createTopic(TOPIC_NAME);
            Queue queue = session.createQueue(TOPIC_NAME); // Same name but different type
            
            // They should not be equal even with the same name
            assertNotEquals(topic, queue);
            assertNotEquals(queue, topic);
        }
    }
}