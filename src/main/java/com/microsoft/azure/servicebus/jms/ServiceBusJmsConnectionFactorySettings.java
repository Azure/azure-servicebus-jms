// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

public class ServiceBusJmsConnectionFactorySettings {
    // a flag added to the AMQP connection to indicate it's a ServiceBus ConnectionFactory client
    static final String IsClientProvider = "com.microsoft:is-client-provider";
    private long connectionIdleTimeoutMS;
    private boolean traceFrames;
    
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
    
    String toQuery() {
        StringBuilder builder = new StringBuilder();
        if (connectionIdleTimeoutMS > 0) {
            appendQuery(builder, "amqp.idleTimeout", String.valueOf(connectionIdleTimeoutMS));
        }
        if (traceFrames) {
            appendQuery(builder, "amqp.traceFrames", "true");
        }
        return builder.toString();
    }
    
    private static void appendQuery(StringBuilder builder, String key, String val) {
        builder.append(builder.length() == 0 ? "?" : "&").append(key).append("=").append(val);
    }
}
