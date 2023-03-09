// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.servicebus.jms;

public final class ClientConstants {
	private ClientConstants() { }

	static final String END_POINT_FORMAT = "amqps://%s.servicebus.windows.net";
	public static final int DEFAULT_OPERATION_TIMEOUT_IN_SECONDS = 30;
}