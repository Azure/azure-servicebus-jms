package com.microsoft.azure.servicebus.jms.aad;

import java.util.ArrayList;
import java.util.List;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;

public class Utility {
	
	public static final ServiceBusJmsConnectionFactorySettings CONNECTION_SETTINGS;
	public static final String TENANT_ID;
	public static final String CLIENT_SECRET;
	public static final String CLIENT_ID;
	public static final String CLIENT_ID_MSI;
	public static final String HOST;
	public static final long DEFAULT_TIMEOUT;
	public static final String TEST_CONNECTION_STRING;
	
	private static final String AUDIENCE = "https://servicebus.azure.net/.default";
    private static final String AUTHORITY = "https://login.microsoftonline.com/%s/";

    static {
        CONNECTION_SETTINGS = new ServiceBusJmsConnectionFactorySettings(120000, true);
        TENANT_ID =  System.getenv("AZURE_TENANT_ID");
        CLIENT_SECRET = System.getenv("AZURE_CLIENT_SECRET");
        CLIENT_ID = System.getenv("AZURE_CLIENT_ID");
        CLIENT_ID_MSI = System.getenv("CLIENT_ID_MSI");
        HOST = System.getenv("HOST_NAMESPACE");
        DEFAULT_TIMEOUT = 3000;
        TEST_CONNECTION_STRING = System.getenv("SERVICE_BUS_CONNECTION_STRING");
    }
    
    public static TokenCredential GetSecretCredential()
	{
		String authority =  String.format(AUTHORITY, Utility.TENANT_ID);
		
		List<String> scopes = new ArrayList<String>();
	    scopes.add(AUDIENCE);

	    TokenCredential credential = new ClientSecretCredentialBuilder()
	    		.tenantId(TENANT_ID)
	    		.clientId(CLIENT_ID)
	    		.clientSecret(CLIENT_SECRET)
	    		.authorityHost(authority)	    		
	    		.build();

		    return credential;
	}
    
    public static TokenCredential GetMsiCredential()
	{
    	TokenCredential credential = new DefaultAzureCredentialBuilder()
				  .managedIdentityClientId(CLIENT_ID)
				  .build();
	    	
		    return credential;		          
	}
}