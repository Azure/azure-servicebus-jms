// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.apache.qpid.jms.JmsQueue;
import org.apache.qpid.jms.JmsTopic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for equals, hashCode, and toString methods of ServiceBusJmsQueue and ServiceBusJmsTopic
 * These tests don't require actual Service Bus connections.
 */
public class ServiceBusJmsDestinationEqualsHashCodeUnitTest {
    
    private static final String QUEUE_NAME = "test-queue";
    private static final String TOPIC_NAME = "test-topic";
    private static final String DIFFERENT_QUEUE_NAME = "different-queue";
    private static final String DIFFERENT_TOPIC_NAME = "different-topic";

    @Test
    public void testServiceBusJmsQueueEquality() {
        // Create ServiceBusJmsQueue instances with same queue name
        ServiceBusJmsQueue queue1 = new ServiceBusJmsQueue(new JmsQueue(QUEUE_NAME));
        ServiceBusJmsQueue queue2 = new ServiceBusJmsQueue(new JmsQueue(QUEUE_NAME));
        
        // Test equality
        assertEquals(queue1, queue2);
        assertEquals(queue2, queue1);
        assertEquals(queue1, queue1); // reflexivity
        
        // Create queue with different name
        ServiceBusJmsQueue differentQueue = new ServiceBusJmsQueue(new JmsQueue(DIFFERENT_QUEUE_NAME));
        assertNotEquals(queue1, differentQueue);
        assertNotEquals(differentQueue, queue1);
    }

    @Test
    public void testServiceBusJmsQueueHashCode() {
        // Create ServiceBusJmsQueue instances with same queue name
        ServiceBusJmsQueue queue1 = new ServiceBusJmsQueue(new JmsQueue(QUEUE_NAME));
        ServiceBusJmsQueue queue2 = new ServiceBusJmsQueue(new JmsQueue(QUEUE_NAME));
        
        // Hash codes should be equal for equal objects
        assertEquals(queue1.hashCode(), queue2.hashCode());
        
        // Test with different queue name
        ServiceBusJmsQueue differentQueue = new ServiceBusJmsQueue(new JmsQueue(DIFFERENT_QUEUE_NAME));
        // Different objects may have different hash codes (not required but likely)
        assertNotEquals(queue1.hashCode(), differentQueue.hashCode());
    }

    @Test
    public void testServiceBusJmsQueueToString() {
        ServiceBusJmsQueue queue = new ServiceBusJmsQueue(new JmsQueue(QUEUE_NAME));
        String toString = queue.toString();
        
        // toString should contain the queue name and class name
        assertTrue(toString.contains(QUEUE_NAME));
        assertTrue(toString.contains("ServiceBusJmsQueue"));
        
        // toString should be consistent
        assertEquals(toString, queue.toString());
    }

    @Test
    public void testServiceBusJmsTopicHashCode() {
        // Create ServiceBusJmsTopic instances with same topic name
        ServiceBusJmsTopic topic1 = new ServiceBusJmsTopic(new JmsTopic(TOPIC_NAME));
        ServiceBusJmsTopic topic2 = new ServiceBusJmsTopic(new JmsTopic(TOPIC_NAME));
        
        // Hash codes should be equal for equal objects
        assertEquals(topic1.hashCode(), topic2.hashCode());
        
        // Test with different topic name
        ServiceBusJmsTopic differentTopic = new ServiceBusJmsTopic(new JmsTopic(DIFFERENT_TOPIC_NAME));
        // Different objects may have different hash codes (not required but likely)
        assertNotEquals(topic1.hashCode(), differentTopic.hashCode());
    }

    @Test
    public void testServiceBusJmsTopicToString() {
        ServiceBusJmsTopic topic = new ServiceBusJmsTopic(new JmsTopic(TOPIC_NAME));
        String toString = topic.toString();
        
        // toString should contain the topic name and class name
        assertTrue(toString.contains(TOPIC_NAME));
        assertTrue(toString.contains("ServiceBusJmsTopic"));
        
        // toString should be consistent
        assertEquals(toString, topic.toString());
    }

    @Test
    public void testNullSafety() {
        ServiceBusJmsQueue queue = new ServiceBusJmsQueue(new JmsQueue(QUEUE_NAME));
        ServiceBusJmsTopic topic = new ServiceBusJmsTopic(new JmsTopic(TOPIC_NAME));
        
        // Should not equal null
        assertNotEquals(queue, null);
        assertNotEquals(topic, null);
        
        // Should not equal objects of different types
        assertNotEquals(queue, "string");
        assertNotEquals(topic, "string");
        assertNotEquals(queue, topic);
    }
}