// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredentialBuilder;

public class AadAuthentication {

    private final String AUDIENCE = "https://servicebus.azure.net/.default";
    private TokenCredential credential;
    private AccessToken currentAccessToken;
    
	public AadAuthentication() {
		this(new DefaultAzureCredentialBuilder().build());
	}

	public AadAuthentication(TokenCredential credential) {
		this.credential = credential;
		this.currentAccessToken = this.generateAccessToken();
	}

	private AccessToken generateAccessToken() {
		return this.credential.getToken(new TokenRequestContext().addScopes(AUDIENCE)).block();
	}
		
	public String getAadToken() {
		//Generate new token if expire
		if (this.currentAccessToken.isExpired()) {
			synchronized(this.currentAccessToken) {
				this.currentAccessToken = this.generateAccessToken(); 
			}
		}
		
		return this.currentAccessToken.getToken();
	}
}
