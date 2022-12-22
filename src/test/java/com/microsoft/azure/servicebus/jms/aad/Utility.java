package com.microsoft.azure.servicebus.jms.aad;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

public class Utility {
	
	public static final ServiceBusJmsConnectionFactorySettings CONNECTION_SETTINGS;
	public static final String TENANT_ID;
	public static final String CLIENT_SECRET;
	public static final String CLIENT_ID;
	public static final String CLIENT_ID_MSI;
	public static final String HOST;
	public static final long DEFAULT_TIMEOUT;
	
	private static final String AUDIENCE = "https://servicebus.azure.net/.default";
    private static final String AUTHORITY = "https://login.microsoftonline.com/%s/";
	    
    public static MessageConsumer consumer = null;
    public static MessageProducer producer = null;
    public static TextMessage receivedMessage = null;
    public static TextMessage message = null;
    
    static {
        CONNECTION_SETTINGS = new ServiceBusJmsConnectionFactorySettings(120000, true);
        TENANT_ID =  System.getenv("AZURE_TENANT_ID");
        CLIENT_SECRET = System.getenv("AZURE_CLIENT_SECRET");
        CLIENT_ID = System.getenv("AZURE_CLIENT_ID");
        CLIENT_ID_MSI = System.getenv("CLIENT_ID_MSI");
        HOST = "jmsTest.servicebus.windows.net";//System.getenv("HOST_NAMESPACE");
        DEFAULT_TIMEOUT = 3000;
    }
    
    public static String GetToken()
	{
		String authority =  String.format(AUTHORITY, Utility.TENANT_ID);
		
		List<String> scopes = new ArrayList<String>();
	    scopes.add(AUDIENCE);

	    ClientSecretCredential credential = new ClientSecretCredentialBuilder()
	    		.tenantId(Utility.TENANT_ID)
	    		.clientId(Utility.CLIENT_ID)
	    		.clientSecret(Utility.CLIENT_SECRET)
	    		.authorityHost(authority)	    		
	    		.build();
	    
	    	TokenRequestContext cxt = new TokenRequestContext();
	    	//cxt.setTenantId(Utility.TENANT_ID);
	    	cxt.setScopes(scopes);   
	    	
		    return credential
	        		.getToken(cxt).block()
	                .getToken();		          
	}
    
    public static void Send(Connection connection, Session session, String entityName, boolean isQueue, int messageSize) throws Exception
	{
		Queue queue = null;
	    Topic topic = null;
	    
		Destination destination = null;
		
		try {
			if (isQueue)
			{
				queue = session.createQueue(entityName);
				destination = queue;
			}
			else {
				topic = session.createTopic(entityName);
				destination = topic;			
			}
			
			producer = session.createProducer(destination);
            String requestText = "Hello jms!";
            message = session.createTextMessage(requestText);
            
            producer.send(message);
            System.out.println("Sending One message...");
            System.out.println("Message sent");
            System.out.println();
            
            System.out.println("Sending 5 message.....");
            for(int i = 0; i<messageSize; i++)
            {
            	producer.send(message);
            	System.out.println("Message " + i + " sent....");             
            }
 
        } finally {
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
	}
    
    public static void ConsumerQueue(Connection connection, String entityName, int messageCount) throws Exception
	{
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = null;
	    
		try 
		{
			queue = session.createQueue(entityName);
			consumer = session.createConsumer(queue);
			receivedMessage = (TextMessage)consumer.receive(DEFAULT_TIMEOUT);
            System.out.println("Receiving message...");
            System.out.println();
            assertNotNull(receivedMessage.getText());
            
            for(int i = 0; i<messageCount; i++)
            {                            	
            	receivedMessage = (TextMessage)consumer.receive(DEFAULT_TIMEOUT);
            	System.out.println("Receiving " + i + " message..."); 
            	assertNotNull(Utility.receivedMessage.getText());               
            }
        } 
		finally 
		{
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
	}
	
	public static void ConsumerTopic(Connection connection, String clientId, String topicName, String subscriptionName, int messageCount) throws JMSException
	{
		connection.setClientID(clientId);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic topic = null;

		try 
		{
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			topic = session.createTopic(topicName);

			consumer = session.createDurableConsumer(topic, subscriptionName);
	        receivedMessage = (TextMessage)consumer.receive(Utility.DEFAULT_TIMEOUT);
	        System.out.println("Receiving message...");
	        System.out.println();
	        assertNotNull(receivedMessage.getText());

	        System.out.println("Sending 5 message.....");
	        for(int i = 0; i<messageCount; i++)
	        {                            	
	        	receivedMessage = (TextMessage)consumer.receive(Utility.DEFAULT_TIMEOUT);
	        	System.out.println("Receiving " + i + " message..."); 
	        	assertNotNull(receivedMessage.getText());               
	        }
		} 
		finally 
		{
	        if (session != null) session.close();
	        if (connection != null) connection.close();
	    }
	}
}