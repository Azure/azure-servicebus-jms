// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map;

import io.netty.handler.proxy.ProxyHandler;

public class ServiceBusJmsConnectionFactorySettings {
    private static Map<String, String> DefaultConfigurationOptions = getDefaultConfigurationOptions();
    
    static final String Vendor = "com.microsoft";
    // a flag added to the AMQP connection to indicate it's a ServiceBus ConnectionFactory client
    static final String IsClientProvider = Vendor + ":is-client-provider";
    static final String QueueAutoDeleteOnIdleDurationInSecondsName = Vendor + ":queue-auto-delete-on-idle-duration-in-seconds";
    static final String TopicAutoDeleteOnIdleDurationInSecondsName = Vendor + ":topic-auto-delete-on-idle-duration-in-seconds";
    static final String SubscriberAutoDeleteOnIdleDurationInSecondsName = Vendor + ":subscriber-auto-delete-on-idle-duration-in-seconds";
    private long connectionIdleTimeoutMS;
    private Supplier<ProxyHandler> proxyHandlerSupplier;
    private boolean traceFrames;
    private long queueAutoDeleteOnIdleDurationInSeconds;
    private long topicAutoDeleteOnIdleDurationInSeconds;
    private long subscriberAutoDeleteOnIdleDurationInSeconds;
    // QPID configuration options https://qpid.apache.org/releases/qpid-jms-0.53.0/docs/index.html
    // It could be AMQP, JMS or other configurations used in the connection URI.
    private Map<String, String> configurationOptions;
    // QPID reconnect options
    private boolean shouldReconnect = true; // Reconnect will happen by default
    private String[] reconnectHosts;
    private Long initialReconnectDelay;
    private Long reconnectDelay;
    private Long maxReconnectDelay;
    private Boolean useReconnectBackOff;
    private Double reconnectBackOffMultiplier;
    private Integer maxReconnectAttempts;
    private Integer startupMaxReconnectAttempts;
    private Integer warnAfterReconnectAttempts;
    private Boolean reconnectRandomize;
    private ReconnectAmqpOpenServerListAction reconnectAmqpOpenServerListAction;
    
    public ServiceBusJmsConnectionFactorySettings() { }

    public ServiceBusJmsConnectionFactorySettings(long connectionIdleTimeoutMS, boolean traceFrames) {
        this.connectionIdleTimeoutMS = connectionIdleTimeoutMS;
        this.traceFrames = traceFrames;
    }

    /**
     * Configure QPID JMS case sensitive options for this ConnectionFactory.
     * Please see <a href="https://qpid.apache.org/releases/qpid-jms-0.41.0/docs/index.html#jms-configuration-options">
     * https://qpid.apache.org/releases/qpid-jms-0.41.0/docs/index.html#jms-configuration-options</a> for the complete list of options.
     */
    public ServiceBusJmsConnectionFactorySettings(Map<String, String> configurationOptions) {
        this.configurationOptions = configurationOptions;
    }
    
    public long getConnectionIdleTimeoutMS() {
        return connectionIdleTimeoutMS;
    }
    
    public void setConnectionIdleTimeoutMS(long connectionIdleTimeoutMS) {
        this.connectionIdleTimeoutMS = connectionIdleTimeoutMS;
    }
    
    public Supplier<ProxyHandler> getProxyHandlerSupplier() {
        return proxyHandlerSupplier;
    }

    public void setProxyHandlerSupplier(Supplier<ProxyHandler> proxyHandlerSupplier) {
        this.proxyHandlerSupplier = proxyHandlerSupplier;
    }
    
    public boolean isTraceFrames() {
        return traceFrames;
    }
    
    public void setTraceFrames(boolean traceFrames) {
        this.traceFrames = traceFrames;
    }

    /**
     * @return the queueAutoDeleteOnIdleDurationInSeconds setting configured for the factory
     */
	public long getQueueAutoDeleteOnIdleDurationInSeconds() {
		return this.queueAutoDeleteOnIdleDurationInSeconds;
	}

	/**
	 * @param queueAutoDeleteOnIdleDurationInSeconds - the autoDeleteOnIdleDurationInSeconds to be set for all queues created using this factory.
	 * If different queues need different AutoDeleteOnIdleDuration settings, use different ServiceBusJmsConnectionFactory to create those queues.
	 */
	public void setQueueAutoDeleteOnIdleDurationInSeconds(long queueAutoDeleteOnIdleDurationInSeconds) {
		this.queueAutoDeleteOnIdleDurationInSeconds = queueAutoDeleteOnIdleDurationInSeconds;
	}

	/**
	 * @return the topicAutoDeleteOnIdleDurationInSeconds setting configured for the factory
	 */
	public long getTopicAutoDeleteOnIdleDurationInSeconds() {
		return this.topicAutoDeleteOnIdleDurationInSeconds;
	}

	/**
	 * @param topicAutoDeleteOnIdleDurationInSeconds - the autoDeleteOnIdleDurationInSeconds to be set for all topics created using this factory.
	 * If different topics need different AutoDeleteOnIdleDuration settings, use different ServiceBusJmsConnectionFactory to create those topics.
	 */
	public void setTopicAutoDeleteOnIdleDurationInSeconds(long topicAutoDeleteOnIdleDurationInSeconds) {
		this.topicAutoDeleteOnIdleDurationInSeconds = topicAutoDeleteOnIdleDurationInSeconds;
	}
    
	/**
	 * @return the subscriberAutoDeleteOnIdleDurationInSeconds setting configured for the factory
	 */
	public long getSubscriberAutoDeleteOnIdleDurationInSeconds() {
		return this.subscriberAutoDeleteOnIdleDurationInSeconds;
	}

	/**
	 * @param subscriberAutoDeleteOnIdleDurationInSeconds - the autoDeleteOnIdleDurationInSeconds to be set for all subscriptions created using this factory.
	 * If different subscriptions need different AutoDeleteOnIdleDuration settings, use different ServiceBusJmsConnectionFactory to create those subscriptions.
	 */
	public void setSubscriberAutoDeleteOnIdleDurationInSeconds(long subscriberAutoDeleteOnIdleDurationInSeconds) {
		this.subscriberAutoDeleteOnIdleDurationInSeconds = subscriberAutoDeleteOnIdleDurationInSeconds;
	}

	public Map<String, String> getConfigurationOptions() {
        return this.configurationOptions;
    }
    
    /**
     * @return True if the reconnect functionalities implement by QPID should be leveraged. Default is true.
     */
    public boolean shouldReconnect() {
        return shouldReconnect;
    }

    /**
     * @param shouldReconnect True if the reconnect functionalities implement by QPID should be leveraged. Default is true.
     */
    public void setShouldReconnect(boolean shouldReconnect) {
        this.shouldReconnect = shouldReconnect;
    }
    
    /**
     * @return The array of ServiceBus hosts that the client should reconnect to in case of a connection failure.
     */
    public String[] getReconnectHosts() {
        return reconnectHosts;
    }

    /**
     * @param reconnectHosts The array of ServiceBus hosts that the client should reconnect to in case of a connection failure.
     *                       An example value is contoso.servicebus.windows.net.
     *                       Please note that the same SAS keys from the original host will be used upon the reconnct host for authentication.
     *                          
     */
    public void setReconnectHosts(String[] reconnectHosts) {
        this.reconnectHosts = reconnectHosts;
    }
    
    /**
     * @return The amount of time the client will wait before the first attempt to reconnect to a remote peer in milliseconds. 
     */
    public Long getInitialReconnectDelay() {
        return initialReconnectDelay;
    }

    /**
     * @param initialReconnectDelay The amount of time the client will wait before the first attempt to reconnect to a remote peer in milliseconds. 
     *                              The default value is zero, meaning the first attempt happens immediately.
     */
    public void setInitialReconnectDelay(long initialReconnectDelay) {
        this.initialReconnectDelay = initialReconnectDelay;
    }

    /**
     * @return The delay between successive reconnection attempts in milliseconds.
     */
    public Long getReconnectDelay() {
        return reconnectDelay;
    }

    /**
     * 
     * @param reconnectDelay The delay between successive reconnection attempts in milliseconds, defaults to 10.
     *                       If the backoff option is not enabled this value remains constant.
     */
    public void setReconnectDelay(long reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    /**
     * @return The maximum time that the client will wait before attempting a reconnect in milliseconds.
     */
    public Long getMaxReconnectDelay() {
        return maxReconnectDelay;
    }

    /**
     * @param maxReconnectDelay The maximum time that the client will wait before attempting a reconnect in milliseconds, defaults to 30000.
     */
    public void setMaxReconnectDelay(long maxReconnectDelay) {
        this.maxReconnectDelay = maxReconnectDelay;
    }

    /**
     * @return True if the time between reconnection attempts should grow based on a configured multiplier.
     */
    public Boolean useReconnectBackOff() {
        return useReconnectBackOff;
    }

    /**
     * @param useReconnectBackOff True if the time between reconnection attempts should grow based on a configured multiplier. This option defaults to true.
     */
    public void setUseReconnectBackOff(boolean useReconnectBackOff) {
        this.useReconnectBackOff = useReconnectBackOff;
    }

    /**
     * @return The multiplier used to grow the reconnection delay value.
     */
    public Double getReconnectBackOffMultiplier() {
        return reconnectBackOffMultiplier;
    }

    /**
     * @param reconnectBackOffMultiplier The multiplier used to grow the reconnection delay value, defaults to 2.0.
     */
    public void setReconnectBackOffMultiplier(double reconnectBackOffMultiplier) {
        this.reconnectBackOffMultiplier = reconnectBackOffMultiplier;
    }

    /**
     * @return The number of reconnection attempts allowed before reporting the connection as failed to the client.
     */
    public Integer getMaxReconnectAttempts() {
        return maxReconnectAttempts;
    }

    /**
     * @param maxReconnectAttempts The number of reconnection attempts allowed before reporting the connection as failed to the client. The default is no limit or (-1).
     */
    public void setMaxReconnectAttempts(int maxReconnectAttempts) {
        this.maxReconnectAttempts = maxReconnectAttempts;
    }

    /**
     * @return For a client that has never connected to a remote peer before this option control how many attempts are made to connect before reporting the connection as failed.
     */
    public Integer getStartupMaxReconnectAttempts() {
        return startupMaxReconnectAttempts;
    }

    /**
     * @param startupMaxReconnectAttempts For a client that has never connected to a remote peer before this option control how many attempts are made to connect before reporting the connection as failed. 
     *                                    The default is to use the value of maxReconnectAttempts.
     */
    public void setStartupMaxReconnectAttempts(int startupMaxReconnectAttempts) {
        this.startupMaxReconnectAttempts = startupMaxReconnectAttempts;
    }

    /**
     * @return Number of reconnection attempts before the client will log a message indicating that reconnect reconnection is being attempted.
     */
    public Integer getWarnAfterReconnectAttempts() {
        return warnAfterReconnectAttempts;
    }

    /**
     * @param warnAfterReconnectAttempts Number of reconnection attempts before the client will log a message indicating that reconnect reconnection is being attempted. The default is to log every 10 connection attempts.
     */
    public void setWarnAfterReconnectAttempts(int warnAfterReconnectAttempts) {
        this.warnAfterReconnectAttempts = warnAfterReconnectAttempts;
    }

    /**
     * @return True if the set of reconnect URIs is randomly shuffled prior to attempting to connect to one of them. This can help to distribute client connections more evenly across multiple remote peers.
     */
    public Boolean shouldReconnectRandomize() {
        return reconnectRandomize;
    }

    /**
     * @param randomize True if the set of reconnect URIs is randomly shuffled prior to attempting to connect to one of them. 
     *                  This can help to distribute client connections more evenly across multiple remote peers. The default value is false.
     */
    public void setReconnectRandomize(boolean randomize) {
        this.reconnectRandomize = randomize;
    }

    /** 
     * @return Returns how the reconnect transport behaves when the connection Open frame from the remote peer provides a list of reconnect hosts to the client.
     * This option accepts one of three values; REPLACE, ADD, or IGNORE (default is REPLACE).
     * If REPLACE is configured then all reconnect URIs other than the one for the current server are replaced with those provided by the remote peer.
     * If ADD is configured then the URIs provided by the remote are added to the existing set of reconnect URIs, with de-duplication.
     * If IGNORE is configured then any updates from the remote are dropped and no changes are made to the set of reconnect URIs in use.
     */
    public ReconnectAmqpOpenServerListAction getReconnectAmqpOpenServerListAction() {
        return reconnectAmqpOpenServerListAction;
    }

    /**
     * @param amqpOpenServerListAction Controls how the reconnect transport behaves when the connection Open frame from the remote peer provides a list of reconnect hosts to the client.
     *                                 This option accepts one of three values; REPLACE, ADD, or IGNORE (default is REPLACE).
     *                                 If REPLACE is configured then all reconnect URIs other than the one for the current server are replaced with those provided by the remote peer.
     *                                 If ADD is configured then the URIs provided by the remote are added to the existing set of reconnect URIs, with de-duplication.
     *                                 If IGNORE is configured then any updates from the remote are dropped and no changes are made to the set of reconnect URIs in use.
     */
    public void setReconnectAmqpOpenServerListAction(ReconnectAmqpOpenServerListAction amqpOpenServerListAction) {
        this.reconnectAmqpOpenServerListAction = amqpOpenServerListAction;
    }
    
    String getServiceBusQuery() {
        StringBuilder builder = new StringBuilder();
        if (connectionIdleTimeoutMS > 0) {
            appendQuery(builder, "amqp.idleTimeout", String.valueOf(connectionIdleTimeoutMS));
        }
        
        if (traceFrames) {
            appendQuery(builder, "amqp.traceFrames", "true");
        }
        
        if (this.configurationOptions == null) {
            this.configurationOptions = new HashMap<String, String>();
        }
        
        // Append the default options if the ones provided by the user does not contain it.
        // Since these are query parameters, they are case sensitive.
        for (String defaultOption : DefaultConfigurationOptions.keySet()) {
            configurationOptions.putIfAbsent(defaultOption, DefaultConfigurationOptions.get(defaultOption));
        }
        
        for (String option : configurationOptions.keySet()) {
            appendQuery(builder, option, configurationOptions.get(option));
        }
        
        return builder.toString();
    }
    
    String getReconnectQuery() {
        StringBuilder queryBuilder = new StringBuilder();
        
        if (initialReconnectDelay != null) {
            appendQuery(queryBuilder, "failover.initialReconnectDelay", String.valueOf(initialReconnectDelay));
        }
        
        if (reconnectDelay != null) {
            appendQuery(queryBuilder, "failover.reconnectDelay", String.valueOf(reconnectDelay));
        }
        
        if (maxReconnectDelay != null) {
            appendQuery(queryBuilder, "failover.maxReconnectDelay", String.valueOf(maxReconnectDelay));
        }
        
        if (useReconnectBackOff != null) {
            appendQuery(queryBuilder, "failover.useReconnectBackOff", String.valueOf(useReconnectBackOff));
        }
        
        if (reconnectBackOffMultiplier != null) {
            appendQuery(queryBuilder, "failover.reconnectBackOffMultiplier", String.valueOf(reconnectBackOffMultiplier));
        }
        
        if (maxReconnectAttempts != null) {
            appendQuery(queryBuilder, "failover.maxReconnectAttempts", String.valueOf(maxReconnectAttempts));
        }
        
        if (startupMaxReconnectAttempts != null) {
            appendQuery(queryBuilder, "failover.startupMaxReconnectAttempts", String.valueOf(startupMaxReconnectAttempts));
        }
        
        if (warnAfterReconnectAttempts != null) {
            appendQuery(queryBuilder, "failover.warnAfterReconnectAttempts", String.valueOf(warnAfterReconnectAttempts));
        }

        if (reconnectRandomize != null) {
            appendQuery(queryBuilder, "failover.randomize", String.valueOf(reconnectRandomize));
        }
        
        if (reconnectAmqpOpenServerListAction != null) {
            appendQuery(queryBuilder, "failover.amqpOpenServerListAction", reconnectAmqpOpenServerListAction.name());
        }
        
        return queryBuilder.toString();
    }
    
    private void appendQuery(StringBuilder builder, String key, String value) {
        if (builder == null) {
            builder = new StringBuilder();
        }
        
        builder.append((builder.length() == 0) ? "?" : "&").append(key).append("=").append(value);
    }
    
    private static Map<String, String> getDefaultConfigurationOptions() {
        Map<String, String> defaultConfigurationOptions = new HashMap<String, String>();
        defaultConfigurationOptions.put("jms.prefetchPolicy.all", "0");
        return defaultConfigurationOptions;
    }
}
