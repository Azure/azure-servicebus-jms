// Copyright (c) Microsoft. All rights reserved.
package com.microsoft.azure.servicebus.jms.aad;

import org.junit.Test;

import com.azure.identity.DefaultAzureCredentialBuilder;

public class TopicTest  {
	
	TestInitialization initialization;
	
	@Test
	public void e2ETipicTestWithSecretCredential()throws Exception {
		System.out.println("\nTest send and received topic on the app using SecretCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("endToEndSecretCredentialTopic");
		
		this.initialization.startAad("Topic", this.initialization.GetClientSecretCredential());
		this.initialization.jmsContext.setClientID("SecretCredentialId");
		
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
	
	@Test
	public void e2ETopicTestWithMsiCredential()throws Exception {
		System.out.println("\nTest send and received topic on the app using MsiCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("endToEndMsiTopic");
		
		this.initialization.startAad("Topic", this.initialization.GetMsiCredential());
		this.initialization.jmsContext.setClientID("MsiCredentialId");
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
	
	@Test
	public void e2ETopicTestWithDefaultAzureCredential()throws Exception {
		System.out.println("\nTest send and received topic on the app using DefaultAzureCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("endToEndDefaultAzureCredentialTopic");
		
		this.initialization.startAad("Topic", new DefaultAzureCredentialBuilder().build());
		this.initialization.jmsContext.setClientID("DefaultAzureCredentialId");
		
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
	
	/*
	 * PORTAL
	 * This test assume entity exist on portal
	 */
	
	@Test
	public void portalE2ETopicTestWithSecret()throws Exception {
		System.out.println("\nTest send and received topic portal test using SecretCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("PortalSecretTopic");
		
		this.initialization.startAad("Topic", this.initialization.GetClientSecretCredential());
		this.initialization.jmsContext.setClientID("PortalSecretId");
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
	
	@Test
	public void portalE2ETopicTestWithMsi()throws Exception {
		System.out.println("\nTest send and received topic portal test using MsiCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("PortalMsiTopic");
		
		this.initialization.startAad("Topic", this.initialization.GetMsiCredential());
		this.initialization.jmsContext.setClientID("PortalMsiId");
		
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
	
	@Test
	public void portalE2ETopicTestWithDefaultAzure()throws Exception {
		System.out.println("\nTest send and received topic portal test using DefaultAzureCredential");
		System.out.println("---------------------------------------------------------");
		this.initialization = new TestInitialization("PortalDefaultAzureTopic");
		
		this.initialization.startAad("Topic", new DefaultAzureCredentialBuilder().build());
		this.initialization.jmsContext.setClientID("PortalDefaultAzureId");
		
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