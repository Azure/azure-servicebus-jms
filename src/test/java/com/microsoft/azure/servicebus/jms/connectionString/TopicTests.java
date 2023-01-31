package com.microsoft.azure.servicebus.jms.connectionString;

import org.junit.Test;
import com.microsoft.azure.servicebus.jms.aad.TestInitialization;

public class TopicTests {	

	TestInitialization initialization;
	
	@Test
	public void sendToTopicOnly()throws Exception {
		System.out.println("\nTest send and creating queue on the app using connection string");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("sendOnlyCsTopic");
		this.initialization.initializeWithSas("Topic");
		this.initialization.entity = initialization.jmsContext.createTopic(initialization.entityName);
        System.out.println("Topic was created.......");
        
        this.initialization.producer = initialization.jmsContext.createProducer();
        System.out.println("Producer was created.....");

		//send message
	    try {
	    	this.initialization.send(10);
	    }catch (Exception e) {
	    	e.printStackTrace();
	    	throw new Exception(e);
	    }finally {
	    	this.initialization.jmsContext.close();
	    	System.out.println("---------------------------------------------------------");
		    System.out.println("End Test");
		    System.out.println("=========================================================");
	    	
	    }		
	}
		
	@Test
	public void endToEndTopicTest()throws Exception {
		System.out.println("\nTest send and received topic on the app using connection string");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("endToEndTopic");
		
		this.initialization.initializeWithSas("Topic");
		this.initialization.jmsContext.setClientID("connectionStringId");
		
		this.initialization.topic = initialization.jmsContext.createTopic(initialization.entityName);
		this.initialization.entity = this.initialization.topic;
        System.out.println("Topic was created.......");
        
		this.initialization.consumer = initialization.jmsContext.createDurableConsumer(initialization.topic, "subDefaultAzure");
	    System.out.println("Consumer was created.....");
	    
	    try {
	    	this.initialization.send(10);
	    	this.initialization.received(10);
	    }catch (Exception e) {
	    	e.printStackTrace();
	    	throw new Exception(e);
	    }finally {
	    	this.initialization.jmsContext.close();
	    	this.initialization.consumer.close();
	    	System.out.println("---------------------------------------------------------");
		    System.out.println("End Test");
		    System.out.println("=========================================================");
	    }	    
	}	
}