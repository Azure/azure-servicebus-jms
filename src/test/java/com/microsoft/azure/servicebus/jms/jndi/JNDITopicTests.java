// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms.jndi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.Session;
import javax.naming.Reference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactory;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsTopic;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsTopicConnectionFactory;
import com.microsoft.azure.servicebus.jms.ConnectionStringBuilder;

public class JNDITopicTests {
    ServiceBusJmsConnectionFactory sbConnectionFactory;
    ServiceBusJmsTopicConnectionFactory sbTopicConnectionFactory;
    String topicName;

    @BeforeEach
    public void testInitialize() {
        ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(TestUtils.TEST_CONNECTION_STRING);
        sbConnectionFactory = new ServiceBusJmsConnectionFactory(connectionStringBuilder, null);
        sbTopicConnectionFactory = new ServiceBusJmsTopicConnectionFactory(connectionStringBuilder, null);
    }
    
    @Test
    public void storeAndCreateTopicThroughSessionTest() throws Exception {
        Connection connection = null;
        TopicConnection topicConnection = null;
        Session session = null;
        
        try {
            // ConnectionFactory -> Connection -> Session -> Topic
            connection = sbConnectionFactory.createConnection();
            session = connection.createSession();
            topicName = "MyTopic-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingTopic(session.createTopic(topicName));
        } finally {
            if (session != null) session.close();
            if (topicConnection != null) topicConnection.close();
            if (connection != null) connection.close();
        }
        
        try {
            // TopicConnectionFactory -> Connection -> Session -> Topic
            connection = sbTopicConnectionFactory.createConnection();
            session = connection.createSession();
            topicName = "MyTopic-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingTopic(session.createTopic(topicName));
        } finally {
            if (session != null) session.close();
            if (topicConnection != null) topicConnection.close();
            if (connection != null) connection.close();
        }

        try {
            // ConnectionFactory -> TopicConnection -> Session -> Topic
            topicConnection = sbConnectionFactory.createTopicConnection();
            session = topicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topicName = "MyTopic-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingTopic(session.createTopic(topicName));
        } finally {
            if (session != null) session.close();
            if (topicConnection != null) topicConnection.close();
            if (connection != null) connection.close();
        }
        
        try {
            // TopicConnectionFactory -> TopicConnection -> Session -> Topic
            topicConnection = sbTopicConnectionFactory.createTopicConnection();
            session = topicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topicName = "MyTopic-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingTopic(session.createTopic(topicName));
        } finally {
            if (session != null) session.close();
            if (topicConnection != null) topicConnection.close();
            if (connection != null) connection.close();
        }
    }
    
    @Test
    public void storeAndCreateTopicThroughTopicSessionTest() throws Exception {
        TopicConnection topicConnection = null;
        TopicSession session = null;

        try {
            // ConnectionFactory -> TopicConnection -> TopicSession -> Topic
            topicConnection = sbConnectionFactory.createTopicConnection();
            session = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topicName = "MyTopic-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingTopic(session.createTopic(topicName));
        } finally {
            if (session != null) session.close();
            if (topicConnection != null) topicConnection.close();
        }
        
        try {
            // TopicConnectionFactory -> TopicConnection -> TopicSession -> Topic
            topicConnection = sbTopicConnectionFactory.createTopicConnection();
            session = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topicName = "MyTopic-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingTopic(session.createTopic(topicName));
        } finally {
            if (session != null) session.close();
            if (topicConnection != null) topicConnection.close();
        }
    }
    
    @Test
    public void storeAndCreateTopicThroughJmsContextTest() throws Exception {
        JMSContext jmsContext = null;
        
        try {
            // ConnectionFactory -> JMSContext -> Topic
            jmsContext = sbConnectionFactory.createContext();
            topicName = "MyTopic-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingTopic(jmsContext.createTopic(topicName));
        } finally {
            if (jmsContext != null) jmsContext.close();
        }
        
        try {
            // TopicConnectionFactory -> JMSContext -> Topic
            jmsContext = sbTopicConnectionFactory.createContext();
            topicName = "MyTopic-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingTopic(jmsContext.createTopic(topicName));
        } finally {
            if (jmsContext != null) jmsContext.close();
        }
    }
    
    // 'physicalName' is a mandatory field, users should not be able to do JNDI without setting it on the topic.
    @Test
    public void emptyNameTest() throws Exception {
        Connection topicConnection = sbConnectionFactory.createConnection();
        Session session = topicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        ServiceBusJmsTopic sbJmsTopic = (ServiceBusJmsTopic)session.createTopic("");
        TestUtils.testInvalidJNDIStorable(sbJmsTopic, "physicalName");
        
        sbJmsTopic = (ServiceBusJmsTopic)session.createTopic(null);
        TestUtils.testInvalidJNDIStorable(sbJmsTopic, "physicalName");
    }
    
    private void testStoringAndRecreatingTopic(Topic topic) throws Exception {
        ServiceBusJmsTopic sbJmsTopic = (ServiceBusJmsTopic)topic;
        Reference reference = sbJmsTopic.getReference();
        assertNotNull(reference);
        
        JNDIReferenceFactory referenceFactory = new JNDIReferenceFactory();
        sbJmsTopic = (ServiceBusJmsTopic)referenceFactory.getObjectInstance(reference, null, null, null);
        assertEquals(topicName, sbJmsTopic.getTopicName());
        
        // Test to see if the ServiceBusJmsTopic is usable by a Session
        try (Connection connection = sbConnectionFactory.createConnection()) {
            try (Session session = connection.createSession()) {
                try (MessageProducer producer = session.createProducer(sbJmsTopic)) {
                    producer.send(session.createMessage());
                }
                
                try (MessageConsumer consumer = session.createConsumer(sbJmsTopic)) {
                    consumer.receive(2000);
                }
            }
        }
        
        // Test to see if the ServiceBusJmsTopic is usable by a TopicSession
        try (TopicConnection topicConnection = sbTopicConnectionFactory.createTopicConnection()) {
            try (TopicSession session = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)) {
                try (TopicPublisher producer = session.createPublisher(sbJmsTopic)) {
                    producer.send(session.createMessage());
                }

                try (MessageConsumer consumer = session.createConsumer(sbJmsTopic)) {
                    consumer.receive(2000);
                }
            }
        }
        
        // Test to see if the ServiceBusJmsTopic is usable by a JMSContext
        try (JMSContext jmsContext = sbConnectionFactory.createContext()) {
            JMSProducer producer = jmsContext.createProducer();
            producer.send(sbJmsTopic, jmsContext.createMessage());
            
            JMSConsumer consumer = jmsContext.createConsumer(sbJmsTopic);
            consumer.receive(2000);
        }
    }
}
