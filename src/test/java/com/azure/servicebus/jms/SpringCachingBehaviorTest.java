// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.Session;
import jakarta.jms.Queue;
import jakarta.jms.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.azure.servicebus.jms.jndi.TestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Integration test that simulates Spring's CachingConnectionFactory behavior
 * to verify that ServiceBusJmsQueue and ServiceBusJmsTopic properly support
 * producer caching through correct equals() and hashCode() implementations.
 */
public class SpringCachingBehaviorTest {
    private ServiceBusJmsConnectionFactory connectionFactory;
    private static final String QUEUE_NAME = "test-cache-queue";
    private static final String TOPIC_NAME = "test-cache-topic";

    @BeforeEach
    public void setUp() {
        ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(TestUtils.TEST_CONNECTION_STRING);
        connectionFactory = new ServiceBusJmsConnectionFactory(connectionStringBuilder, null);
    }

    /**
     * Simulates Spring's DestinationCacheKey behavior for queues
     */
    @Test
    public void testQueueDestinationCacheKeyBehavior() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            // Create multiple queue instances with same name (simulating different session calls)
            Queue queue1 = session.createQueue(QUEUE_NAME);
            Queue queue2 = session.createQueue(QUEUE_NAME);
            Queue queue3 = session.createQueue(QUEUE_NAME);
            
            // Simulate Spring's DestinationCacheKey creation and caching
            DestinationCacheKey key1 = new DestinationCacheKey(queue1);
            DestinationCacheKey key2 = new DestinationCacheKey(queue2);
            DestinationCacheKey key3 = new DestinationCacheKey(queue3);
            
            // All cache keys should be equal
            assertEquals(key1, key2);
            assertEquals(key2, key3);
            assertEquals(key1, key3);
            
            // All cache keys should have the same hash code
            assertEquals(key1.hashCode(), key2.hashCode());
            assertEquals(key2.hashCode(), key3.hashCode());
            
            // Simulate the producer cache
            Map<DestinationCacheKey, String> producerCache = new HashMap<>();
            producerCache.put(key1, "cached-producer-1");
            
            // All keys should retrieve the same cached producer
            assertEquals("cached-producer-1", producerCache.get(key1));
            assertEquals("cached-producer-1", producerCache.get(key2));
            assertEquals("cached-producer-1", producerCache.get(key3));
            
            // Cache should only contain one entry
            assertEquals(1, producerCache.size());
        }
    }

    /**
     * Simulates Spring's DestinationCacheKey behavior for topics
     */
    @Test
    public void testTopicDestinationCacheKeyBehavior() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            // Create multiple topic instances with same name (simulating different session calls)
            Topic topic1 = session.createTopic(TOPIC_NAME);
            Topic topic2 = session.createTopic(TOPIC_NAME);
            Topic topic3 = session.createTopic(TOPIC_NAME);
            
            // Simulate Spring's DestinationCacheKey creation and caching
            DestinationCacheKey key1 = new DestinationCacheKey(topic1);
            DestinationCacheKey key2 = new DestinationCacheKey(topic2);
            DestinationCacheKey key3 = new DestinationCacheKey(topic3);
            
            // All cache keys should be equal
            assertEquals(key1, key2);
            assertEquals(key2, key3);
            assertEquals(key1, key3);
            
            // All cache keys should have the same hash code
            assertEquals(key1.hashCode(), key2.hashCode());
            assertEquals(key2.hashCode(), key3.hashCode());
            
            // Simulate the producer cache
            Map<DestinationCacheKey, String> producerCache = new HashMap<>();
            producerCache.put(key1, "cached-producer-1");
            
            // All keys should retrieve the same cached producer
            assertEquals("cached-producer-1", producerCache.get(key1));
            assertEquals("cached-producer-1", producerCache.get(key2));
            assertEquals("cached-producer-1", producerCache.get(key3));
            
            // Cache should only contain one entry
            assertEquals(1, producerCache.size());
        }
    }

    /**
     * Tests that different destinations (different names) create different cache keys
     */
    @Test
    public void testDifferentDestinationsCreateDifferentCacheKeys() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            Queue queue1 = session.createQueue("queue-1");
            Queue queue2 = session.createQueue("queue-2");
            Topic topic1 = session.createTopic("topic-1");
            Topic topic2 = session.createTopic("topic-2");
            
            DestinationCacheKey queueKey1 = new DestinationCacheKey(queue1);
            DestinationCacheKey queueKey2 = new DestinationCacheKey(queue2);
            DestinationCacheKey topicKey1 = new DestinationCacheKey(topic1);
            DestinationCacheKey topicKey2 = new DestinationCacheKey(topic2);
            
            // Different destinations should create different cache keys
            assertNotEquals(queueKey1, queueKey2);
            assertNotEquals(topicKey1, topicKey2);
            assertNotEquals(queueKey1, topicKey1);
            
            // Test caching behavior
            Map<DestinationCacheKey, String> producerCache = new HashMap<>();
            producerCache.put(queueKey1, "queue-1-producer");
            producerCache.put(queueKey2, "queue-2-producer");
            producerCache.put(topicKey1, "topic-1-producer");
            producerCache.put(topicKey2, "topic-2-producer");
            
            assertEquals(4, producerCache.size());
            assertEquals("queue-1-producer", producerCache.get(queueKey1));
            assertEquals("queue-2-producer", producerCache.get(queueKey2));
            assertEquals("topic-1-producer", producerCache.get(topicKey1));
            assertEquals("topic-2-producer", producerCache.get(topicKey2));
        }
    }

    /**
     * Tests the toString() behavior for debugging purposes
     */
    @Test
    public void testToStringForDebugging() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            Queue queue = session.createQueue(QUEUE_NAME);
            Topic topic = session.createTopic(TOPIC_NAME);
            
            String queueString = queue.toString();
            String topicString = topic.toString();
            
            // toString should contain meaningful information
            assertTrue(queueString.contains("ServiceBusJmsQueue"));
            assertTrue(queueString.contains(QUEUE_NAME));
            
            assertTrue(topicString.contains("ServiceBusJmsTopic"));
            assertTrue(topicString.contains(TOPIC_NAME));
            
            // Different destinations should have different string representations
            assertNotEquals(queueString, topicString);
        }
    }

    /**
     * Simplified DestinationCacheKey implementation to simulate Spring's behavior
     */
    private static class DestinationCacheKey {
        private final Destination destination;

        public DestinationCacheKey(Destination destination) {
            this.destination = destination;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DestinationCacheKey that = (DestinationCacheKey) obj;
            return destination != null ? destination.equals(that.destination) : that.destination == null;
        }

        @Override
        public int hashCode() {
            return destination != null ? destination.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "DestinationCacheKey{destination=" + destination + "}";
        }
    }
}