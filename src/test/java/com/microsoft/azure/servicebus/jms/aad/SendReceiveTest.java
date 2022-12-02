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
import org.junit.Before;
import org.junit.Test;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactory;

public class SendReceiveTest 
{
	private static final String AUDIENCE = "https://servicebus.azure.net/.default";
    private static final String AUTHORITY = "https://login.microsoftonline.com/%s/";
    
    private MessageConsumer consumer = null;
    private MessageProducer producer = null;
    private TextMessage receivedMessage = null;
    private TextMessage message = null;
    
    ServiceBusJmsConnectionFactory sbConnectionFactoryExtensionToken;
    ServiceBusJmsConnectionFactory sbConnectionFactoryUserToken;
    
    @Before
    public void Initialize() 
    {
		String token = GetToken();
    	sbConnectionFactoryExtensionToken = new ServiceBusJmsConnectionFactory(Utility.TENANT_ID, Utility.CLIENT_ID, Utility.CLIENT_SECRET, Utility.HOST, Utility.CONNECTION_SETTINGS);
    	sbConnectionFactoryUserToken = new ServiceBusJmsConnectionFactory(token, Utility.HOST, Utility.CONNECTION_SETTINGS, true);
    }
	
	@Test
    public void SendReceiveQueueWithTokenGenerated() throws Exception  
    {		    
		Connection sendConnection = sbConnectionFactoryExtensionToken.createConnection();
		Connection receiveConnection = sbConnectionFactoryExtensionToken.createConnection();
       
		System.out.println();
        System.out.println("Testing Queue with Token Generated");
        Send(sendConnection, "generatedtoken", true);
        ConsumerQueue(receiveConnection, "generatedtoken");
	}
	
	@Test
    public void SendReceiveQueueUserToken() throws Exception 
    {
		Connection sendConnection = sbConnectionFactoryUserToken.createConnection();
		Connection receiveConnection = sbConnectionFactoryUserToken.createConnection();
	    
        System.out.println();
        System.out.println("Testing Queue with User Token");
        Send(sendConnection, "usertoken", true);
        ConsumerQueue(receiveConnection, "usertoken");
    }
	
	@Test
    public void SendReceiveTopicWithTokenGenerated() throws Exception 
    {
		Connection sendConnection = sbConnectionFactoryExtensionToken.createConnection();
		Connection receiveConnection = sbConnectionFactoryExtensionToken.createConnection();
	
	    System.out.println();
        System.out.println("Testing Topic with Token Generated");
        Send(sendConnection, "topicgeneratedtoken", false);
        ConsumerTopic(receiveConnection, "TokenGenerated", "topicgeneratedtoken", "subGenToken");
    }
	
	@Test
    public void SendReceiveTopicWithUserToken() throws Exception 
    {		
		Connection sendConnection = sbConnectionFactoryUserToken.createConnection();
		Connection receiveConnection = sbConnectionFactoryUserToken.createConnection();	
		
		System.out.println();
        System.out.println("Testing Topic with Token Generated");		
		Send(sendConnection, "topicusertoken", false);
		ConsumerTopic(receiveConnection, "UserToken", "topicusertoken", "subUserToken");
    }
	
	public void Send(Connection connection, String entityName, boolean isQueue) throws Exception
	{
		Queue queue = null;
	    Topic topic = null;
	    
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
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
            for(int i = 0; i<5; i++)
            {
            	producer.send(message);
            	System.out.println("Message " + i + " sent....");              
            }
 
        } finally {
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
	}
	
	public void ConsumerQueue(Connection connection, String entityName) throws Exception
	{
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = null;
	    
		try 
		{
			queue = session.createQueue(entityName);
			consumer = session.createConsumer(queue);
            receivedMessage = (TextMessage)consumer.receive(Utility.DEFAULT_TIMEOUT);
            System.out.println("Receiving message...");
            System.out.println();
            assertNotNull(receivedMessage.getText());
            
            for(int i = 0; i<6; i++)
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
	
	private void ConsumerTopic(Connection connection, String clientId, String topicName, String subscriptionName) throws JMSException
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
	        for(int i = 0; i<5; i++)
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
	
	private String GetToken()
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
	    	cxt.setTenantId(Utility.TENANT_ID);
	    	cxt.setScopes(scopes);

		    return credential
	        		.getToken(cxt).block()
	                .getToken();		          
	}
}