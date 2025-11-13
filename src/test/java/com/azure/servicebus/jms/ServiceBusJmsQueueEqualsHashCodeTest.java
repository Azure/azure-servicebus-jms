// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.jms.Connection;
import jakarta.jms.Session;
import jakarta.jms.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.azure.servicebus.jms.jndi.TestUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServiceBusJmsQueueEqualsHashCodeTest {
    private ServiceBusJmsConnectionFactory connectionFactory;
    private static final String QUEUE_NAME = "test-queue";
    private static final String DIFFERENT_QUEUE_NAME = "different-queue";

    @BeforeEach
    public void setUp() {
        ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(TestUtils.TEST_CONNECTION_STRING);
        connectionFactory = new ServiceBusJmsConnectionFactory(connectionStringBuilder, null);
    }

    @Test
    public void testQueueEquality() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            // Create two queues with the same name
            Queue queue1 = session.createQueue(QUEUE_NAME);
            Queue queue2 = session.createQueue(QUEUE_NAME);
            
            // They should be equal
            assertEquals(queue1, queue2);
            assertEquals(queue2, queue1);
            
            // Reflexivity
            assertEquals(queue1, queue1);
            
            // Create a queue with different name
            Queue differentQueue = session.createQueue(DIFFERENT_QUEUE_NAME);
            
            // They should not be equal
            assertNotEquals(queue1, differentQueue);
            assertNotEquals(differentQueue, queue1);
        }
    }

    @Test
    public void testQueueHashCode() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            // Create two queues with the same name
            Queue queue1 = session.createQueue(QUEUE_NAME);
            Queue queue2 = session.createQueue(QUEUE_NAME);
            
            // They should have the same hash code
            assertEquals(queue1.hashCode(), queue2.hashCode());
            
            // Create a queue with different name
            Queue differentQueue = session.createQueue(DIFFERENT_QUEUE_NAME);
            
            // Different queues may have different hash codes (not required, but likely)
            // We don't assert inequality here as hash collisions are allowed
        }
    }

    @Test
    public void testQueueToString() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            Queue queue = session.createQueue(QUEUE_NAME);
            String toString = queue.toString();
            
            // toString should contain the queue name
            assertTrue(toString.contains(QUEUE_NAME));
            assertTrue(toString.contains("ServiceBusJmsQueue"));
            
            // toString should be consistent
            assertEquals(toString, queue.toString());
        }
    }

    @Test
    public void testNullEquality() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            Queue queue = session.createQueue(QUEUE_NAME);
            
            // Queue should not equal null
            assertNotEquals(queue, null);
            assertNotEquals(null, queue);
        }
    }

    @Test
    public void testDifferentClassEquality() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            Queue queue = session.createQueue(QUEUE_NAME);
            String string = "not a queue";
            
            // Queue should not equal object of different class
            assertNotEquals(queue, string);
        }
    }

    @Test
    public void testSpringCachingSimulation() throws Exception {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession()) {
            
            // Simulate Spring's CachingConnectionFactory behavior
            Map<Queue, String> producerCache = new HashMap<>();
            
            // Create multiple queue instances with same name
            Queue queue1 = session.createQueue(QUEUE_NAME);
            Queue queue2 = session.createQueue(QUEUE_NAME);
            Queue queue3 = session.createQueue(QUEUE_NAME);
            
            // Simulate caching producers
            producerCache.put(queue1, "producer1");
            
            // All instances should retrieve the same cached producer
            assertEquals("producer1", producerCache.get(queue1));
            assertEquals("producer1", producerCache.get(queue2));
            assertEquals("producer1", producerCache.get(queue3));
            
            // Cache should only contain one entry
            assertEquals(1, producerCache.size());
        }
    }
}