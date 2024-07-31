package com.azure.servicebus.jms.connection.string;

import com.azure.servicebus.jms.ServiceBusJmsConnectionFactory;
import com.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.qpid.jms.policy.JmsDefaultPrefetchPolicy;
import org.junit.jupiter.api.Test;

public class ServiceBusJMSConnectionFactoryUriBuilderTest {

	@Test
	public void testSingleHostNoOptions() {
		Map<String, String> configurationOptions = new HashMap<>();
		configurationOptions.put("jms.prefetchPolicy.all", "20");
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings(
				configurationOptions);
		String host = "foo.servicebus.windows.net";
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		// Passing no options but failover is true by default
		String expectedUri = "failover:(amqps://foo.servicebus.windows.net)?jms.prefetchPolicy.all=20";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
	}

	@Test
	public void testSingleHostWithJmsPrefetch() {
		Map<String, String> configurationOptions = new HashMap<>();
		configurationOptions.put("jms.prefetchPolicy.all", "20");
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings(
				configurationOptions);
		String host = "foo.servicebus.windows.net";
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		// Passing no options but failover is true by default
		String expectedUri = "failover:(amqps://foo.servicebus.windows.net)?jms.prefetchPolicy.all=20";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
		JmsDefaultPrefetchPolicy prefetchPolicy = (JmsDefaultPrefetchPolicy) factory.getPrefetchPolicy();
		assertEquals(20, prefetchPolicy.getQueuePrefetch());
	}

	@Test
	public void testSingleHostNoOptionsWithoutDefaultReconnect() {
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings();
		settings.setShouldReconnect(false);
		String host = "foo.servicebus.windows.net";
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		// Passing no options with reconnect disabled.
		String expectedUri = "amqps://foo.servicebus.windows.net?jms.prefetchPolicy.all=0";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
	}

	@Test
	public void testSingleHostNoOptionsWithoutDefaultReconnectWithJMSPrefetch() {
		Map<String, String> configurationOptions = new HashMap<>();
		configurationOptions.put("jms.prefetchPolicy.all", "20");
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings(
				configurationOptions);
		settings.setShouldReconnect(false);
		String host = "foo.servicebus.windows.net";
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		// Passing no options with reconnect disabled.
		String expectedUri = "amqps://foo.servicebus.windows.net?jms.prefetchPolicy.all=20";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
	}

	@Test
	public void testSingleHostWithAmqpOptionsWithDefaultReconnect() {
		String host = "foo.servicebus.windows.net";
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings();
		settings.setConnectionIdleTimeoutMS(20000);
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		String expectedUri = "failover:(amqps://foo.servicebus.windows.net?amqp.idleTimeout=20000)?jms.prefetchPolicy.all=0";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
	}

	@Test
	public void testSingleHostWithAmqpOptionsWithoutDefaultReconnect() {
		String host = "foo.servicebus.windows.net";
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings();
		settings.setShouldReconnect(false);
		settings.setConnectionIdleTimeoutMS(20000);
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		String expectedUri = "amqps://foo.servicebus.windows.net?amqp.idleTimeout=20000&jms.prefetchPolicy.all=0";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
	}

	@Test
	public void testSingleHostWithAmqpOptionsWithDefaultReconnectAndFailoverOptions() {
		String host = "foo.servicebus.windows.net";
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings();
		settings.setConnectionIdleTimeoutMS(20000);
		settings.setMaxReconnectAttempts(3);
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		String expectedUri = "failover:(amqps://foo.servicebus.windows.net?amqp.idleTimeout=20000)?failover.maxReconnectAttempts=3&jms.prefetchPolicy.all=0";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
	}

	@Test
	public void testMultipleHostsWithAmqpOptionsWithDefaultReconnectAndFailoverOptions() {
		String host = "foo.servicebus.windows.net";
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings();
		settings.setMaxReconnectAttempts(3);
		settings.setConnectionIdleTimeoutMS(20000);
		String[] reconnectHosts = { "bar.servicebus.windows.net" };
		settings.setReconnectHosts(reconnectHosts);
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		String expectedUri = "failover:(amqps://foo.servicebus.windows.net?amqp.idleTimeout=20000,amqps://bar.servicebus.windows.net?amqp.idleTimeout=20000)?failover.maxReconnectAttempts=3&jms.prefetchPolicy.all=0";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
	}

	@Test
	public void testMultipleHostsWithJustJMSPrefetech() {
		String host = "foo.servicebus.windows.net";
		Map<String, String> configurationOptions = new HashMap<>();
		configurationOptions.put("jms.prefetchPolicy.all", "20");
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings(
				configurationOptions);
		String[] reconnectHosts = { "bar.servicebus.windows.net" };
		settings.setReconnectHosts(reconnectHosts);
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		String expectedUri = "failover:(amqps://foo.servicebus.windows.net,amqps://bar.servicebus.windows.net)?jms.prefetchPolicy.all=20";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
		JmsDefaultPrefetchPolicy prefetchPolicy = (JmsDefaultPrefetchPolicy) factory.getPrefetchPolicy();
		assertEquals(20, prefetchPolicy.getQueuePrefetch());
	}

	@Test
	public void testMultipleHostsWithAmqpOptionsWithoutDefaultReconnectAndFailoverOptions() {
		String host = "foo.servicebus.windows.net";
		Map<String, String> configurationOptions = new HashMap<>();
		configurationOptions.put("jms.prefetchPolicy.all", "20");
		ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings(
				configurationOptions);
		settings.setShouldReconnect(false);
		settings.setConnectionIdleTimeoutMS(20000);
		// Even though options like maxReconnectAttempts and reconnect hosts are
		// specified, since reconnect is set to false, there should be no failover
		// options
		// added and only AmqpOptions and JMSProviderOptions should be formed.
		settings.setMaxReconnectAttempts(3);
		String[] reconnectHosts = { "bar.servicebus.windows.net" };
		settings.setReconnectHosts(reconnectHosts);
		ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory("user", "pass", host, settings);

		String expectedUri = "amqps://foo.servicebus.windows.net?amqp.idleTimeout=20000&jms.prefetchPolicy.all=20";
		String actualUri = factory.getRemoteConnectionUri();
		assertEquals(expectedUri, actualUri);
		JmsDefaultPrefetchPolicy prefetchPolicy = (JmsDefaultPrefetchPolicy) factory.getPrefetchPolicy();
		assertEquals(20, prefetchPolicy.getQueuePrefetch());
	}
}
