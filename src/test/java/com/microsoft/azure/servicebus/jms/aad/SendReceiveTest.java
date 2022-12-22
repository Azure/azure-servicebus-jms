package com.microsoft.azure.servicebus.jms.aad;

import javax.jms.Connection;
import javax.jms.Session;
import org.junit.Before;
import org.junit.Test;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactory;

public class SendReceiveTest {
	ServiceBusJmsConnectionFactory sbConnectionFactoryAppServiceToken;
	ServiceBusJmsConnectionFactory sbConnectionFactoryMsiToken;

	// App service
	@Test
	public void SendReceiveQueueWithClientSecrete() throws Exception {
		String queueName = "appServiceTokenQueue";
		sbConnectionFactoryAppServiceToken = new ServiceBusJmsConnectionFactory(Utility.TENANT_ID, Utility.CLIENT_ID,
				Utility.CLIENT_SECRET, Utility.HOST, Utility.CONNECTION_SETTINGS);
		Connection sendConnection = sbConnectionFactoryAppServiceToken.createConnection();
		Connection receiveConnection = sbConnectionFactoryAppServiceToken.createConnection();
		sendConnection.start();
		Session session = sendConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		System.out.println();
		System.out.println("Testing Queue with token from appService using tenant id, client id and client secret");
		Utility.Send(sendConnection, session, queueName, true, 5);
		Utility.ConsumerQueue(receiveConnection, queueName, 5);
	}

	@Test
	public void SendReceiveTopicWithClientSecrete() throws Exception {
		String topic = "secretCredentialToken";
		String subscription = "secretJms";
		String clientId = "clientIdJmsTest";

		sbConnectionFactoryAppServiceToken = new ServiceBusJmsConnectionFactory(Utility.TENANT_ID, Utility.CLIENT_ID,
				Utility.CLIENT_SECRET, Utility.HOST, Utility.CONNECTION_SETTINGS);

		Connection sendConnection = sbConnectionFactoryAppServiceToken.createConnection();
		Connection receiveConnection = sbConnectionFactoryAppServiceToken.createConnection();
		sendConnection.start();
		Session session = sendConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		System.out.println();
		System.out.println("Testing Topic with token from appService using tenant id, client id and client secret");
		// Utility.Send(sendConnection, session, topic, false, 5);
		Utility.ConsumerTopic(receiveConnection, clientId, topic, subscription, 5);
	}

	// MSi
	@Test
	public void SendReceiveQueueMsiToken() throws Exception {
		String queueName = "msiTest";
		sbConnectionFactoryMsiToken = new ServiceBusJmsConnectionFactory(Utility.CONNECTION_SETTINGS, Utility.HOST,
				Utility.CLIENT_ID_MSI, Utility.TENANT_ID);
		Connection sendConnection = sbConnectionFactoryMsiToken.createConnection();
		Connection receiveConnection = sbConnectionFactoryMsiToken.createConnection();
		sendConnection.start();
		Session session = sendConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		System.out.println();
		System.out.println("Testing Queue with Token using msi");
		Utility.Send(sendConnection, session, queueName, true, 5);
		Utility.ConsumerQueue(receiveConnection, queueName, 5);
	}

	/*
	 * @Test public void SendReceiveTopicWitMsiToken() throws Exception { String
	 * topic = "msitesttopic"; String subscription = "sub1";
	 * sbConnectionFactoryMsiToken = new
	 * ServiceBusJmsConnectionFactory(Utility.CONNECTION_SETTINGS, Utility.HOST,
	 * Utility.CLIENT_ID_MSI, Utility.TENANT_ID); Connection sendConnection =
	 * sbConnectionFactoryMsiToken.createConnection(); Connection receiveConnection
	 * = sbConnectionFactoryMsiToken.createConnection(); sendConnection.start();
	 * Session session = sendConnection.createSession(false,
	 * Session.AUTO_ACKNOWLEDGE);
	 * 
	 * System.out.println(); System.out.println("Testing Topic with msi token");
	 * Utility.Send(sendConnection, session, "topicusertoken", false, 5);
	 * Utility.ConsumerTopic(receiveConnection, "msitesttopic", topic, subscription,
	 * 5); }
	 */

	// DefaultAzureCredential
	@Test
	public void SendReceiveQueueDefaultAzureCredential() throws Exception {
		String queueName = "msiTest";
		sbConnectionFactoryMsiToken = new ServiceBusJmsConnectionFactory(Utility.CONNECTION_SETTINGS, Utility.HOST,
				Utility.CLIENT_ID_MSI, Utility.TENANT_ID);
		Connection sendConnection = sbConnectionFactoryMsiToken.createConnection();
		Connection receiveConnection = sbConnectionFactoryMsiToken.createConnection();
		sendConnection.start();
		Session session = sendConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		System.out.println();
		System.out.println("Testing Queue with Token using msi");
		Utility.Send(sendConnection, session, queueName, true, 5);
		Utility.ConsumerQueue(receiveConnection, queueName, 5);
	}

	/*
	 * @Test public void SendReceiveDefaultAzureCredential() throws Exception {
	 * String topic = "msitesttopic"; String subscription = "sub1";
	 * sbConnectionFactoryMsiToken = new
	 * ServiceBusJmsConnectionFactory(Utility.CONNECTION_SETTINGS, Utility.HOST);
	 * Connection sendConnection = sbConnectionFactoryMsiToken.createConnection();
	 * Connection receiveConnection =
	 * sbConnectionFactoryMsiToken.createConnection(); sendConnection.start();
	 * Session session = sendConnection.createSession(false,
	 * Session.AUTO_ACKNOWLEDGE);
	 * 
	 * System.out.println(); System.out.println("Testing Topic with msi token");
	 * Utility.Send(sendConnection, session, "topicusertoken", false, 5);
	 * Utility.ConsumerTopic(receiveConnection, "msitesttopic", topic, subscription,
	 * 5); }
	 */
}