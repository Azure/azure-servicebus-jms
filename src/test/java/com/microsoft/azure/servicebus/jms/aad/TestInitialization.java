// Copyright (c) Microsoft. All rights reserved.
package com.microsoft.azure.servicebus.jms.aad;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactory;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;

public class TestInitialization 
{	
	// Aad
	private final String AUDIENCE = "https://servicebus.azure.net/.default";
    private final String AUTHORITY = "https://login.microsoftonline.com/%s/";
	public String TENANT_ID;
	public String CLIENT_ID;
	public String CLIENT_SECRET;
	public String CLIENT_ID_MSI;
		
	public String CONNECTION_STRING;
	
	// Common between authentication and queue and topic
	public TextMessage sendMsg;
	public TextMessage receivedMsg;
	public String HOST;
	public String entityName;
	public Destination entity;
	public Topic topic;
	public Queue queue;
	public JMSContext jmsContext;
	public JMSProducer producer;
	public JMSConsumer consumer;
	public final long DEFAULT_TIMEOUT = 10000;;
	public ServiceBusJmsConnectionFactory factory;
	public ServiceBusJmsConnectionFactorySettings SETTINGS;
	
	// Topic only
	public String subscriberName;
	
	public TestInitialization() 
	{
		// Aad credential
        this.TENANT_ID =  System.getenv("AZURE_TENANT_ID");
        this.CLIENT_ID = System.getenv("AZURE_CLIENT_ID");
        this.CLIENT_ID_MSI = System.getenv("CLIENT_ID_MSI");
        this.CLIENT_SECRET = System.getenv("AZURE_CLIENT_SECRET");
	}
	
	public TestInitialization(String entityName) 
	{
		// Aad credential
        this.TENANT_ID =  System.getenv("AZURE_TENANT_ID");
        this.CLIENT_ID = System.getenv("AZURE_CLIENT_ID");
        this.CLIENT_ID_MSI = System.getenv("CLIENT_ID_MSI");
        this.CLIENT_SECRET = System.getenv("AZURE_CLIENT_SECRET");
        
        // Primary Connection String
        this.CONNECTION_STRING = System.getenv("SERVICE_BUS_CONNECTION_STRING");
        
        // Common
        this.HOST = System.getenv("HOST_NAMESPACE");
        this.SETTINGS = new ServiceBusJmsConnectionFactorySettings();
        this.entityName = entityName;
	}
	
    public void initializeWithSas(String entityType)
    {
    	this.factory = new ServiceBusJmsConnectionFactory(this.CONNECTION_STRING, this.SETTINGS);
		System.out.println("Factory was created.....");
		
		this.jmsContext = factory.createContext();
        System.out.println("Context was created.....");
	}
    
    public void initializeWithAad(String entityType, TokenCredential credential)
    {
    	this.factory = new ServiceBusJmsConnectionFactory(credential, this.HOST, this.SETTINGS);
		System.out.println("Factory was created.....");
		
		this.jmsContext = factory.createContext();
        System.out.println("Context was created.....");
	}
    
    public void send(int numberOfMessage) {
		System.out.println("\nStart Send Messages to entity " + entityName);
		this.producer = jmsContext.createProducer();
        System.out.println("Producer was created.....");
		
		for(int i=0; i<numberOfMessage; i++) {
			sendMsg = (TextMessage)jmsContext.createTextMessage("Message " + i);
			this.producer.send(entity, sendMsg);
			System.out.println("Send message " + i);
		}	
	}
    
    public void received(int numberOfMessage) throws JMSException 
	{
		System.out.println("\nStart Received Messages to entity " + entityName);
	
		for(int i=0; i<numberOfMessage; i++) {
			receivedMsg = (TextMessage) consumer.receive(DEFAULT_TIMEOUT);
			assertNotNull(receivedMsg.getText());
			System.out.println("Received " + receivedMsg.getText());
		}    	
    }

    public TokenCredential GetClientSecretCredential()
	{
		String authority =  String.format(this.AUTHORITY, this.TENANT_ID);	
		List<String> scopes = new ArrayList<String>();
	    scopes.add(AUDIENCE);
	    System.out.println(TENANT_ID);
	    System.out.println(CLIENT_ID);
	    System.out.println(CLIENT_SECRET);
	    
	    TokenCredential credential = new ClientSecretCredentialBuilder()
	    		.tenantId(TENANT_ID)
	    		.clientId(CLIENT_ID)
	    		.clientSecret(CLIENT_SECRET)
	    		.authorityHost(authority)
	    		.build();

		    return credential;
	}
    
    public TokenCredential GetMsiCredential()
	{
    	System.out.println(CLIENT_ID_MSI);
    	TokenCredential credential = new DefaultAzureCredentialBuilder()
				  .managedIdentityClientId(CLIENT_ID_MSI)
				  .build();
		    return credential;		          
	}
}