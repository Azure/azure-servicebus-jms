// Copyright (c) Microsoft. All rights reserved.
package com.microsoft.azure.servicebus.jms.connectionString;

import javax.jms.TextMessage;
import org.junit.Test;
import com.microsoft.azure.servicebus.jms.aad.TestInitialization;

public class QueueTests {	
    TextMessage msg;
    
	TestInitialization initialization;
	
	@Test
	public void sendToQueueOnly()throws Exception {
		System.out.println("\nTest send and creating queue on the app using connection string");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("sendOnlyCs");
		this.initialization.initializeWithSas("Queue");
		this.initialization.entity = initialization.jmsContext.createQueue(initialization.entityName);
        System.out.println("Queue was created.......");
        
        this.initialization.producer = initialization.jmsContext.createProducer();
        System.out.println("Producer was created.....");

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
	public void receivedToQueueOnly()throws Exception {
		System.out.println("\nTest send and creating queue on the app using connection string");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("sendOnlyCs");
		this.initialization.initializeWithSas("Queue");
		this.initialization.entity = initialization.jmsContext.createQueue(initialization.entityName);
        System.out.println("Queue was created.......");
        
		this.initialization.consumer = initialization.jmsContext.createConsumer(initialization.entity);
	    System.out.println("Consumer was created.....");
	    
	    try {
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
	
	@Test
	public void endToEndQueueTest()throws Exception {
		System.out.println("\nTest send and creating queue on the app using connection string");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("sendOnlyCs");
		this.initialization.initializeWithSas("Queue");
		this.initialization.entity = initialization.jmsContext.createQueue(initialization.entityName);
        System.out.println("Queue was created.......");
        
		this.initialization.consumer = initialization.jmsContext.createConsumer(initialization.entity);
	    System.out.println("Consumer was created.....");
	    
	    //send message
	    try {
	    	this.initialization.send(10);
	    	this.initialization.received(0);
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
