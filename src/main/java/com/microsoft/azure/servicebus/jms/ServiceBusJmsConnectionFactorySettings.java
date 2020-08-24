// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

public class ServiceBusJmsConnectionFactorySettings {
    // a flag added to the AMQP connection to indicate it's a ServiceBus ConnectionFactory client
    static final String IsClientProvider = "com.microsoft:is-client-provider";
    private long connectionIdleTimeoutMS;
    private boolean traceFrames;
    
    // QPID failover options
    private boolean shouldUseFailover = true;
    private String[] failoverHosts;
    private Long failoverInitialReconnectDelay;
    private Long failoverReconnectDelay;
    private Long failoverMaxReconnectDelay;
    private Boolean failoverUseReconnectBackOff;
    private Double failoverReconnectBackOffMultiplier;
    private Integer failoverMaxReconnectAttempts;
    private Integer failoverStartupMaxReconnectAttempts;
    private Integer failoverWarnAfterReconnectAttempts;
    private Boolean failoverRandomize;
    private AmqpOpenServerListAction failoverAmqpOpenServerListAction;
    
    public enum AmqpOpenServerListAction { REPLACE, ADD, IGNORE };
    
    public ServiceBusJmsConnectionFactorySettings() { }
    
    public ServiceBusJmsConnectionFactorySettings(long connectionIdleTimeoutMS, boolean traceFrames) {
        this.connectionIdleTimeoutMS = connectionIdleTimeoutMS;
        this.traceFrames = traceFrames;
    }
    
    public long getConnectionIdleTimeoutMS() {
        return connectionIdleTimeoutMS;
    }
    
    public void setConnectionIdleTimeoutMS(long connectionIdleTimeoutMS) {
        this.connectionIdleTimeoutMS = connectionIdleTimeoutMS;
    }
    
    public boolean isTraceFrames() {
        return traceFrames;
    }
    
    public void setTraceFrames(boolean traceFrames) {
        this.traceFrames = traceFrames;
    }
    
    /**
     * @return True if the failover functionalities implement by QPID should be leveraged. Default is true.
     */
    public boolean shouldUseFailover() {
        return shouldUseFailover;
    }

    /**
     * @param shouldUseFailover True if the failover functionalities implement by QPID should be leveraged. Default is true.
     */
    public void setShouldUseFailover(boolean shouldUseFailover) {
        this.shouldUseFailover = shouldUseFailover;
    }
    
    /**
     * @return The array of ServiceBus hosts that the client should failover to in case of a connection failure.
     */
    public String[] getFailoverHosts() {
        return failoverHosts;
    }

    /**
     * @param failoverHosts The array of ServiceBus hosts that the client should failover to in case of a connection failure.
     *                      An example value is contoso.servicebus.windows.net.
     *                          
     */
    public void setFailoverHosts(String[] failoverHosts) {
        this.failoverHosts = failoverHosts;
    }
    
    /**
     * @param connectionStrings The array of ServiceBus ConnectionStrings that will be parsed to obtain the hosts that 
     *                          the client should failover to in case of a connection failure.
     */
    public void setFailoverHostsByConnectionString(String[] connectionStrings) {
        if (connectionStrings != null) {
            String[] hosts = new String[connectionStrings.length];
            
            for (int i = 0; i < connectionStrings.length; i++) {
                ConnectionStringBuilder builder = new ConnectionStringBuilder(connectionStrings[i]);
                hosts[i] = builder.getEndpoint().getHost();
            }
            
            this.failoverHosts = hosts;
        }
    }
    
    /**
     * @return The amount of time the client will wait before the first attempt to reconnect to a remote peer in milliseconds. 
     */
    public Long getFailoverInitialReconnectDelay() {
        return failoverInitialReconnectDelay;
    }

    /**
     * @param initialReconnectDelay The amount of time the client will wait before the first attempt to reconnect to a remote peer in milliseconds. 
     *                              The default value is zero, meaning the first attempt happens immediately.
     */
    public void setFailoverInitialReconnectDelay(long initialReconnectDelay) {
        this.failoverInitialReconnectDelay = initialReconnectDelay;
    }

    /**
     * @return The delay between successive reconnection attempts in milliseconds.
     */
    public Long getFailoverReconnectDelay() {
        return failoverReconnectDelay;
    }

    /**
     * 
     * @param reconnectDelay The delay between successive reconnection attempts in milliseconds, defaults to 10.
     *                       If the backoff option is not enabled this value remains constant.
     */
    public void setFailoverReconnectDelay(long reconnectDelay) {
        this.failoverReconnectDelay = reconnectDelay;
    }

    /**
     * @return The maximum time that the client will wait before attempting a reconnect in milliseconds.
     */
    public Long getFailoverMaxReconnectDelay() {
        return failoverMaxReconnectDelay;
    }

    /**
     * @param maxReconnectDelay The maximum time that the client will wait before attempting a reconnect in milliseconds, defaults to 30000.
     */
    public void setFailoverMaxReconnectDelay(long maxReconnectDelay) {
        this.failoverMaxReconnectDelay = maxReconnectDelay;
    }

    /**
     * @return True if the time between reconnection attempts should grow based on a configured multiplier.
     */
    public Boolean shouldFailoverUseReconnectBackOff() {
        return failoverUseReconnectBackOff;
    }

    /**
     * @param useReconnectBackOff True if the time between reconnection attempts should grow based on a configured multiplier. This option defaults to true.
     */
    public void setFailoverUseReconnectBackOff(boolean useReconnectBackOff) {
        this.failoverUseReconnectBackOff = useReconnectBackOff;
    }

    /**
     * @return The multiplier used to grow the reconnection delay value.
     */
    public Double getFailoverReconnectBackOffMultiplier() {
        return failoverReconnectBackOffMultiplier;
    }

    /**
     * @param reconnectBackOffMultiplier The multiplier used to grow the reconnection delay value, defaults to 2.0.
     */
    public void setFailoverReconnectBackOffMultiplier(double reconnectBackOffMultiplier) {
        this.failoverReconnectBackOffMultiplier = reconnectBackOffMultiplier;
    }

    /**
     * @return The number of reconnection attempts allowed before reporting the connection as failed to the client.
     */
    public Integer getFailoverMaxReconnectAttempts() {
        return failoverMaxReconnectAttempts;
    }

    /**
     * @param maxReconnectAttempts The number of reconnection attempts allowed before reporting the connection as failed to the client. The default is no limit or (-1).
     */
    public void setFailoverMaxReconnectAttempts(int maxReconnectAttempts) {
        this.failoverMaxReconnectAttempts = maxReconnectAttempts;
    }

    /**
     * @return For a client that has never connected to a remote peer before this option control how many attempts are made to connect before reporting the connection as failed.
     */
    public Integer getFailoverStartupMaxReconnectAttempts() {
        return failoverStartupMaxReconnectAttempts;
    }

    /**
     * @param startupMaxReconnectAttempts For a client that has never connected to a remote peer before this option control how many attempts are made to connect before reporting the connection as failed. 
     *                                    The default is to use the value of maxReconnectAttempts.
     */
    public void setFailoverStartupMaxReconnectAttempts(int startupMaxReconnectAttempts) {
        this.failoverStartupMaxReconnectAttempts = startupMaxReconnectAttempts;
    }

    /**
     * @return Number of reconnection attempts before the client will log a message indicating that failover reconnection is being attempted.
     */
    public Integer getFailoverWarnAfterReconnectAttempts() {
        return failoverWarnAfterReconnectAttempts;
    }

    /**
     * @param warnAfterReconnectAttempts Number of reconnection attempts before the client will log a message indicating that failover reconnection is being attempted. The default is to log every 10 connection attempts.
     */
    public void setFailovertWarnAfterReconnectAttempts(int warnAfterReconnectAttempts) {
        this.failoverWarnAfterReconnectAttempts = warnAfterReconnectAttempts;
    }

    /**
     * @return True if the set of failover URIs is randomly shuffled prior to attempting to connect to one of them. This can help to distribute client connections more evenly across multiple remote peers.
     */
    public Boolean shouldFailoverRandomize() {
        return failoverRandomize;
    }

    /**
     * @param randomize True if the set of failover URIs is randomly shuffled prior to attempting to connect to one of them. 
     *                  This can help to distribute client connections more evenly across multiple remote peers. The default value is false.
     */
    public void setFailoverRandomize(boolean randomize) {
        this.failoverRandomize = randomize;
    }

    /** 
     * Controls how the failover transport behaves when the connection Open frame from the remote peer provides a list of failover hosts to the client.
     * This option accepts one of three values; REPLACE, ADD, or IGNORE (default is REPLACE).
     * <li>If REPLACE is configured then all failover URIs other than the one for the current server are replaced with those provided by the remote peer.</li>
     * <li>If ADD is configured then the URIs provided by the remote are added to the existing set of failover URIs, with de-duplication.</li>
     * <li>If IGNORE is configured then any updates from the remote are dropped and no changes are made to the set of failover URIs in use.</li>
     */
    public AmqpOpenServerListAction getFailoverAmqpOpenServerListAction() {
        return failoverAmqpOpenServerListAction;
    }

    /**
     * Controls how the failover transport behaves when the connection Open frame from the remote peer provides a list of failover hosts to the client.
     * This option accepts one of three values; REPLACE, ADD, or IGNORE (default is REPLACE).
     * <li>If REPLACE is configured then all failover URIs other than the one for the current server are replaced with those provided by the remote peer.</li>
     * <li>If ADD is configured then the URIs provided by the remote are added to the existing set of failover URIs, with de-duplication.</li>
     * <li>If IGNORE is configured then any updates from the remote are dropped and no changes are made to the set of failover URIs in use.</li>
     */
    public void setFailoverAmqpOpenServerListAction(AmqpOpenServerListAction amqpOpenServerListAction) {
        this.failoverAmqpOpenServerListAction = amqpOpenServerListAction;
    }
    
    String getServiceBusQuery() {
        StringBuilder builder = new StringBuilder();
        if (connectionIdleTimeoutMS > 0) {
            appendQuery(builder, "amqp.idleTimeout", String.valueOf(connectionIdleTimeoutMS));
        }
        
        if (traceFrames) {
            appendQuery(builder, "amqp.traceFrames", "true");
        }
        
        return builder.toString();
    }
    
    String getFailoverQuery() {
        StringBuilder queryBuilder = new StringBuilder();
        
        if (failoverInitialReconnectDelay != null) {
            appendQuery(queryBuilder, "failover.initialReconnectDelay", String.valueOf(failoverInitialReconnectDelay));
        }
        
        if (failoverReconnectDelay != null) {
            appendQuery(queryBuilder, "failover.reconnectDelay", String.valueOf(failoverReconnectDelay));
        }
        
        if (failoverMaxReconnectDelay != null) {
            appendQuery(queryBuilder, "failover.maxReconnectDelay", String.valueOf(failoverMaxReconnectDelay));
        }
        
        if (failoverUseReconnectBackOff != null) {
            appendQuery(queryBuilder, "failover.useReconnectBackOff", String.valueOf(failoverUseReconnectBackOff));
        }
        
        if (failoverReconnectBackOffMultiplier != null) {
            appendQuery(queryBuilder, "failover.reconnectBackOffMultiplier", String.valueOf(failoverReconnectBackOffMultiplier));
        }
        
        if (failoverMaxReconnectAttempts != null) {
            appendQuery(queryBuilder, "failover.maxReconnectAttempts", String.valueOf(failoverMaxReconnectAttempts));
        }
        
        if (failoverStartupMaxReconnectAttempts != null) {
            appendQuery(queryBuilder, "failover.startupMaxReconnectAttempts", String.valueOf(failoverStartupMaxReconnectAttempts));
        }
        
        if (failoverWarnAfterReconnectAttempts != null) {
            appendQuery(queryBuilder, "failover.warnAfterReconnectAttempts", String.valueOf(failoverWarnAfterReconnectAttempts));
        }

        if (failoverRandomize != null) {
            appendQuery(queryBuilder, "failover.randomize", String.valueOf(failoverRandomize));
        }
        
        if (failoverAmqpOpenServerListAction != null) {
            appendQuery(queryBuilder, "failover.amqpOpenServerListAction", failoverAmqpOpenServerListAction.name());
        }
        
        return queryBuilder.toString();
    }
    
    private void appendQuery(StringBuilder builder, String key, String value) {
        if (builder == null) {
            builder = new StringBuilder();
        }
        
        builder.append((builder.length() == 0) ? "?" : "&").append(key).append("=").append(value);
    }
}
