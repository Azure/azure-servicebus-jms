// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;

public class AadAuthentication {

    private final String AUDIENCE = "https://servicebus.azure.net/.default";
    private TokenCredential credential;
    private AccessToken currentAccessToken;

    /**
     * Create an AadAuthentication using a token credential.
     * @param credential. Can be of type Azure Default Credential, Msi credential or secret credential.
     */
	public AadAuthentication(TokenCredential credential) {
		this.credential = credential;
		this.currentAccessToken = this.generateAccessToken();
	}
	
	/**
     * Checks if current token is valid and generates a new token if is not valid 
     * @return returns a valid token. 
     */
	public String getAadToken() {
		//Generate new token if expire
		if (this.currentAccessToken.isExpired()) {
			synchronized(this.currentAccessToken) {
				this.currentAccessToken = this.generateAccessToken(); 
			}
		}
		
		return this.currentAccessToken.getToken();
	}
	
	private AccessToken generateAccessToken() {
		return this.credential.getToken(new TokenRequestContext().addScopes(AUDIENCE)).block();
	}
}
