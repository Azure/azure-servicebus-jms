package com.microsoft.azure.servicebus.jms.aad;

import static org.junit.Assert.assertNotNull;

import javax.jms.*;

import org.junit.Before;
import org.junit.Test;

import com.azure.core.credential.TokenCredential;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactory;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;

public class SendReceiveTestUsingContext {
	ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings(3000*60, true);
	String queueName = "Using";
	String topicName = "tp";
    String subscription = "sub";

	//QUEUE TESTS
	// Connection String
  //Notes: When queue does not exit we are able to create and show on portal 
    @Test
	public void E2EContextQueueUsingConectionString() throws Exception {
		System.out.println(" Test E2E Using ConectionString for Queue....");
		System.out.println("---------------------------------------------------------");
		queueName += "CSqueue";
        ConnectionFactory factory = new ServiceBusJmsConnectionFactory(Utility.TEST_CONNECTION_STRING, settings);
        
        JMSContext jmsContext = factory.createContext();
        Queue queue = jmsContext.createQueue(queueName);
     
        System.out.println("Create message....");
        TextMessage msg = (TextMessage)jmsContext.createTextMessage("Hello jms!....");

        System.out.println("Create producer.....");
        JMSProducer producer = jmsContext.createProducer();

        System.out.println("Send message to queue...... " + queueName);
        producer.send(queue, msg);

        System.out.println("Create consumer.....");
        JMSConsumer consumer = jmsContext.createConsumer(queue);

        System.out.println("Receive message");
        msg = (TextMessage) consumer.receive();
        System.out.println("---------------------------------------------------------");
        System.out.println("End Test");
        System.out.println("=========================================================");
	}

	// SecretCredential
	//Notes: When queue does not exit we get an authorize error but if queue already exist we get no error
	//
	@Test
	public void E2EContextQueueUsingSecretCredential() throws Exception {

		System.out.println(" Test E2E Using SecretCredential for Queue....");
		System.out.println("---------------------------------------------------------");
		queueName += "SecretCredentialqueue";
		sendandRecivedQueue(queueName, Utility.GetSecretCredential());
	}
	
	// MSI Credential
	@Test
	public void E2EContextQueueUsingMsiCredential() throws Exception {
		System.out.println(" Test E2E Using Msi for Queue....");
		System.out.println("---------------------------------------------------------");
		queueName += "MsiQueue";
        sendandRecivedQueue(queueName, Utility.GetMsiCredential());
	}
	
	//DefaultAzureCredential
	@Test
	public void E2EContextQueueUsingDefaultAzureCredential() throws Exception {
		System.out.println(" Test E2E Using DefaultAzureCredential Queue....");
		System.out.println("---------------------------------------------------------");
		queueName += "DefaultAzureCredential";
		sendandRecivedQueue(queueName, null);
	}
	
	// Topic Tests 
	@Test
	public void e2EContextTopicUsingConectionString() throws Exception {
		ConnectionFactory factory = new ServiceBusJmsConnectionFactory(Utility.TEST_CONNECTION_STRING, settings);
		String clientId = "clientIdCS";
		JMSProducer producer;
		JMSConsumer consumer;
		
		System.out.println(" Test E2E Using ConectionString for Topic....");
		System.out.println("---------------------------------------------------------");
		topicName += "CSTopic";

		try {
	        JMSContext jmsContext = factory.createContext();
	        jmsContext.setClientID(clientId);
	        Topic topic = jmsContext.createTopic(topicName);
	     
	        System.out.println("Create message....");
	        TextMessage msg = (TextMessage)jmsContext.createTextMessage("Hello jms!....");
	
	        System.out.println("Create producer.....");
	        producer = jmsContext.createProducer();
	
	        System.out.println("Send message to topic...... " + topicName);
	        producer.send(topic, msg);
	
	        System.out.println("Create consumer.....");
	        consumer = jmsContext.createDurableConsumer(topic, subscription);
	       
	        System.out.println("Receive message");
	        msg = (TextMessage) consumer.receive(10000);
	        //assertNotNull(msg.getText());
	        System.out.println("Receiving message..." + msg.getText());
	        consumer.close();
	        System.out.println("---------------------------------------------------------");
	        System.out.println("End Test");
	        System.out.println("=========================================================");
	       
		}catch (Exception e) {
	    	e.printStackTrace();
	    }       
	}

	// SecretCredential
	//Notes: When queue does not exit we get an authorize error but if queue already exist we get no error
	//
	@Test
	public void e2EContextTopicUsingSecretCredential() throws Exception {

		System.out.println(" Test E2E Using SecretCredential for Topic....");
		System.out.println("---------------------------------------------------------");
		String clientId = "clientId-Secrete";
		topicName += "SecreteTopic";
		sendAndRecivedTopicSubscription(topicName, clientId, Utility.GetSecretCredential());
	}
	
	// MSI Credential
	@Test
	public void e2EContextTopicUsingMsiCredential() throws Exception {
		System.out.println(" Test E2E Using Msi for Queue....");
		System.out.println("---------------------------------------------------------");
		String clientId = "clientId-Msi";
		topicName += "MsiTopic";
		sendAndRecivedTopicSubscription(topicName, clientId, Utility.GetMsiCredential());
	}
	
	//DefaultAzureCredential
	@Test
	public void e2EContextTopicUsingDefaultAzureCredential() throws Exception {
		System.out.println(" Test E2E Using DefaultAzureCredential Topic....");
		System.out.println("---------------------------------------------------------");
		String clientId = "DefaultAzure";
		topicName += "DATopic";
		sendAndRecivedTopicSubscription(topicName, clientId, null);
        
	}
	
	public void sendandRecivedQueue(String queueName, TokenCredential credential) {
		
		try{
			ConnectionFactory factory = new ServiceBusJmsConnectionFactory(Utility.GetSecretCredential(), Utility.HOST, settings);
	        JMSContext jmsContext = factory.createContext();
	        System.out.println("Create Queue...." + queueName);
	        Queue queue = jmsContext.createQueue(queueName);
	     
	        System.out.println("Create message....");
	        TextMessage msg = (TextMessage)jmsContext.createTextMessage("Hello jms!....");
	
	        System.out.println("Create producer.....");
	        JMSProducer producer = jmsContext.createProducer();
	
	        System.out.println("Send message to queue...... " + queueName);
	        producer.send(queue, msg);
	
	        System.out.println("Create consumer.....");
	        JMSConsumer consumer = jmsContext.createConsumer(queue);
	
	        System.out.println("Receive message");
	        msg = (TextMessage) consumer.receive();
	        System.out.println("---------------------------------------------------------");
	        System.out.println("End Test");
	        System.out.println("=========================================================");
		}catch (Exception e) {
	    	e.printStackTrace();
	    	sendandRecivedQueue(queueName, credential); 
	    }
	}
	public void sendAndRecivedTopicSubscription(String topicName, String clientId, TokenCredential credential) {
		ConnectionFactory factory = new ServiceBusJmsConnectionFactory(credential, Utility.HOST, settings);
        JMSProducer producer;
		JMSConsumer consumer;
		
		try {
	        JMSContext jmsContext = factory.createContext();
	        jmsContext.setClientID(clientId);
	        Topic topic = jmsContext.createTopic(topicName);
	     
	        System.out.println("Create message....");
	        TextMessage msg = (TextMessage)jmsContext.createTextMessage("Hello jms!....");
	
	        System.out.println("Create producer.....");
	        producer = jmsContext.createProducer();
	
	        System.out.println("Send message to topic...... " + topicName);
	        producer.send(topic, msg);
	
	        System.out.println("Create consumer.....");
	        consumer = jmsContext.createDurableConsumer(topic, subscription);
	        Thread.sleep(1000*60);
	        
	        System.out.println("Receive message");
	        msg = (TextMessage) consumer.receive();
	        assertNotNull(msg.getText());
	        System.out.println("Receiving message..." + msg.getText());
	        consumer.close();
	        System.out.println("---------------------------------------------------------");
	        System.out.println("End Test");
	        System.out.println("=========================================================");
	       
		}catch (Exception e) {
	    	e.printStackTrace();
	    	sendAndRecivedTopicSubscription(topicName, clientId, credential);
	    }
	}
}