package com.microsoft.azure.servicebus.jms;

import java.util.ArrayList;
import java.util.List;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;

public class AadAuthentication {

    private static final String AUDIENCE = "https://servicebus.azure.net/.default";
    private static final String AUTHORITY = "https://login.microsoftonline.com/%s/";
	DefaultAzureCredential credential;
	ClientSecretCredential clientSecretCredential;
	
	public AadAuthentication()
	{
		credential = new DefaultAzureCredentialBuilder()
				  .build();
		
		
	}

	public AadAuthentication(String clientId) {
		credential = new DefaultAzureCredentialBuilder()
				  .managedIdentityClientId(clientId)
				  .build();
	}
	
	public AadAuthentication(String tenantId, String clientId, String clientSecret){
		String authority =  String.format(AUTHORITY, tenantId);
		clientSecretCredential = new ClientSecretCredentialBuilder()
	    		.tenantId(tenantId)
	    		.clientId(clientId)
	    		.clientSecret(clientSecret)
	    		.authorityHost(authority)	    		
	    		.build();
	}
	
	public String GetMsiAndDefaultCredentialToken() {
    	List<String> scopes = new ArrayList<String>();
	    scopes.add(AUDIENCE);

        TokenRequestContext cxt = new TokenRequestContext();
 	    cxt.setScopes(scopes);
 	   
 	    String token = credential
 	    		.getToken(cxt).block()
 	            .getToken();
 	   
 	    return token;
	}
	
	public String GetclientSecretCredentialToken() {
    	List<String> scopes = new ArrayList<String>();
	    scopes.add(AUDIENCE);

        TokenRequestContext cxt = new TokenRequestContext();
        cxt.setScopes(scopes);
 	   
 	    String token = clientSecretCredential
 	    		.getToken(cxt).block()
 	            .getToken();
 	   
 	    return token; 
	}
}
