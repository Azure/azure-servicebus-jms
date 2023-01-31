package com.microsoft.azure.servicebus.jms.aad;

import org.junit.Test;
import com.azure.identity.DefaultAzureCredentialBuilder;

public class QueueTest {
	TestInitialization initialization;
	
	@Test
	public void e2EQueueTestWithSecretCredential()throws Exception {
		System.out.println("\nTest send and received queue on the app using SecretCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("endToEndSecretCredentialQueue");
		
		this.initialization.startAad("Queue", this.initialization.GetClientSecretCredential());
		this.initialization.entity = initialization.jmsContext.createQueue(initialization.entityName);
        System.out.println("Queue was created.......");
        
		this.initialization.consumer = initialization.jmsContext.createConsumer(initialization.entity);
	    System.out.println("Consumer was created.....");
	    
	    //Send message
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
	
	@Test
	public void e2EQueueTestWithMsiCredential()throws Exception {
		System.out.println("\nTest send and received queue on the app using MsiCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("endToEndMsiQueue");
		
		this.initialization.startAad("Queue", this.initialization.GetMsiCredential());
		this.initialization.entity = initialization.jmsContext.createQueue(initialization.entityName);
        System.out.println("Queue was created.......");
        
		this.initialization.consumer = initialization.jmsContext.createConsumer(initialization.entity);
	    System.out.println("Consumer was created.....");
	    
	    //Send message
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
	
	@Test
	public void e2EQueueTestWithDefaultAzureCredential()throws Exception {
		System.out.println("\nTest send and received queue on the app using DefaultAzureCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("endToEndDefaultAzureCredentialQueue");
		
		this.initialization.startAad("Queue", new DefaultAzureCredentialBuilder().build());
		this.initialization.entity = initialization.jmsContext.createQueue(initialization.entityName);
        System.out.println("Queue was created.......");
        
		this.initialization.consumer = initialization.jmsContext.createConsumer(initialization.entity);
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
	
	/*
	 * PORTAL
	 * This test assume entity exist on portal
	 */
	
	@Test
	public void portalE2EQueueTestWithSecret()throws Exception {
		System.out.println("\nTest send and received queue portal test using SecretCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("PortalSecretQueue");
		
		this.initialization.startAad("Queue", this.initialization.GetClientSecretCredential());
		this.initialization.entity = initialization.jmsContext.createQueue(initialization.entityName);
        
		this.initialization.consumer = initialization.jmsContext.createConsumer(initialization.entity);
	    System.out.println("Consumer was created.....");
	    
	    //Send message
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
	
	@Test
	public void portalE2EQueueTestWithMsi()throws Exception {
		System.out.println("\nTest send and received queue portal test using MsiCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("PortalMsiQueue");
		
		this.initialization.startAad("Queue", this.initialization.GetMsiCredential());
		this.initialization.entity = initialization.jmsContext.createQueue(initialization.entityName);
        
		this.initialization.consumer = initialization.jmsContext.createConsumer(initialization.entity);
	    System.out.println("Consumer was created.....");
	    
	    //send message
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
	
	@Test
	public void portalE2EQueueTestWithDefaultAzure()throws Exception {
		System.out.println("\nTest send and received queue portal test using Default Azure Credential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("PortalDefaultAzureQueue");
		
		this.initialization.startAad("Queue", new DefaultAzureCredentialBuilder().build());
		this.initialization.entity = initialization.jmsContext.createQueue(initialization.entityName);
        
		this.initialization.consumer = initialization.jmsContext.createConsumer(initialization.entity);
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