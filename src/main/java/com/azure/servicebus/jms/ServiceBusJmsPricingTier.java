// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

/**
 * Selects the Service Bus JMS provider mode for a connection factory.
 */
public enum ServiceBusJmsPricingTier {
    /**
     * The limited JMS subset supported by Standard namespaces.
     * This mode does not enable full JMS support or automatic entity creation.
     */
    STANDARD,

    /**
     * Full Service Bus JMS provider mode, which requires a Premium namespace.
     */
    PREMIUM
}
