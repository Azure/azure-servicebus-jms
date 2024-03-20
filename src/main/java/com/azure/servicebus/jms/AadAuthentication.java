// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;

class AadAuthentication {

    private final String AUDIENCE = "https://servicebus.azure.net/.default";
    private TokenCredential credential;

    /**
     * Create an AadAuthentication using a token credential.
     * @param credential. Can be of type Azure Default Credential, Msi credential or secret credential.
     */
	public AadAuthentication(TokenCredential credential) {
		this.credential = credential;
	}
	
	/**
     * Return a token based on the credential. No caching is necessary as Azure.Identity
     * library caches the tokens in-memory by default.
     * @return returns a valid token. 
     */
	public String getAadToken() {
		return this.credential.getToken(new TokenRequestContext().addScopes(AUDIENCE)).block().getToken();
	}
}
