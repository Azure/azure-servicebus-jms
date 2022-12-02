package com.microsoft.azure.servicebus.jms.aad;

import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;

public class Utility {
	
	public static final ServiceBusJmsConnectionFactorySettings CONNECTION_SETTINGS;
	public static final String TENANT_ID;
	public static final String CLIENT_SECRET;
	public static final String CLIENT_ID;
	public static final String HOST;
	public static final long DEFAULT_TIMEOUT;
	
    static {
        
        CONNECTION_SETTINGS = new ServiceBusJmsConnectionFactorySettings(120000, true);
        TENANT_ID =  System.getenv("TENANT_ID");
        CLIENT_SECRET = System.getenv("CLIENT_SECRET");
        CLIENT_ID = System.getenv("CLIENT_ID");
        HOST = System.getenv("HOST_NAMESPACE");
        DEFAULT_TIMEOUT = 3000;
    }
}