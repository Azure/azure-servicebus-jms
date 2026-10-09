// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.azure.servicebus.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.naming.Reference;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.servicebus.jms.jndi.JNDIReferenceFactory;
import jakarta.jms.Connection;
import org.apache.qpid.jms.JmsConnectionExtensions;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class ServiceBusJmsPricingTierTest {
    private static final String HOST = "example.servicebus.windows.net";
    private static final String CONNECTION_STRING =
        "Endpoint=sb://" + HOST + "/;SharedAccessKeyName=example;SharedAccessKey=fake";

    @Test
    public void premiumIsTheDefaultForAllSettingsConstructorsAndNullFactorySettings() throws Exception {
        ServiceBusJmsConnectionFactorySettings[] settings = {
            new ServiceBusJmsConnectionFactorySettings(),
            new ServiceBusJmsConnectionFactorySettings(1000, false),
            new ServiceBusJmsConnectionFactorySettings(new HashMap<>())
        };
        for (ServiceBusJmsConnectionFactorySettings value : settings) {
            assertEquals(ServiceBusJmsPricingTier.PREMIUM, value.getPricingTier());
            assertEquals(Boolean.TRUE, properties(new ServiceBusJmsConnectionFactory(CONNECTION_STRING, value))
                .get(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
        }
        assertEquals(Boolean.TRUE, properties(new ServiceBusJmsConnectionFactory(CONNECTION_STRING, null))
            .get(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
    }

    @Test
    public void standardOmitsOnlyTheProviderMarker() throws Exception {
        ServiceBusJmsConnectionFactorySettings standard = settings(ServiceBusJmsPricingTier.STANDARD);
        ServiceBusJmsConnectionFactorySettings premium = settings(ServiceBusJmsPricingTier.PREMIUM);
        ServiceBusJmsConnectionFactory standardFactory = new ServiceBusJmsConnectionFactory(CONNECTION_STRING, standard);
        ServiceBusJmsConnectionFactory premiumFactory = new ServiceBusJmsConnectionFactory(CONNECTION_STRING, premium);
        standardFactory.setCustomUserAgent("example");
        premiumFactory.setCustomUserAgent("example");
        Map<String, Object> expected = properties(premiumFactory);
        assertEquals(Boolean.TRUE, expected.remove(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
        Map<String, Object> actual = properties(standardFactory);
        assertFalse(actual.containsKey(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
        assertEquals(expected, actual);
        assertTrue(actual.get("user-agent").toString().endsWith("/example"));
        assertEquals(300L, actual.get(ServiceBusJmsConnectionFactorySettings.QueueAutoDeleteOnIdleDurationInSecondsName));
        assertEquals(400L, actual.get(ServiceBusJmsConnectionFactorySettings.TopicAutoDeleteOnIdleDurationInSecondsName));
        assertEquals(500L, actual.get(ServiceBusJmsConnectionFactorySettings.SubscriberAutoDeleteOnIdleDurationInSecondsName));
        assertEquals(premiumFactory.getRemoteConnectionUri(), standardFactory.getRemoteConnectionUri());
    }

    @Test
    public void standardAppliesToEveryAuthenticationConstructorAndSpecializedFactory() throws Exception {
        TokenCredential credential = request -> Mono.just(
            new AccessToken("fake", OffsetDateTime.now().plusHours(1)));
        ServiceBusJmsConnectionFactorySettings standard = settings(ServiceBusJmsPricingTier.STANDARD);
        ServiceBusJmsConnectionFactory[] factories = {
            new ServiceBusJmsConnectionFactory(CONNECTION_STRING, standard),
            new ServiceBusJmsConnectionFactory(new ConnectionStringBuilder(CONNECTION_STRING), standard),
            new ServiceBusJmsConnectionFactory("example", "fake", HOST, standard),
            new ServiceBusJmsConnectionFactory(credential, HOST, standard),
            new ServiceBusJmsQueueConnectionFactory(CONNECTION_STRING, standard),
            new ServiceBusJmsQueueConnectionFactory(new ConnectionStringBuilder(CONNECTION_STRING), standard),
            new ServiceBusJmsQueueConnectionFactory("example", "fake", HOST, standard),
            new ServiceBusJmsTopicConnectionFactory(CONNECTION_STRING, standard),
            new ServiceBusJmsTopicConnectionFactory(new ConnectionStringBuilder(CONNECTION_STRING), standard),
            new ServiceBusJmsTopicConnectionFactory("example", "fake", HOST, standard)
        };
        for (ServiceBusJmsConnectionFactory factory : factories) {
            assertFalse(properties(factory).containsKey(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
        }
    }

    @Test
    public void factoriesCanSelectDifferentTiersAndExplicitPremiumRetainsTheMarker() throws Exception {
        ServiceBusJmsConnectionFactory standard =
            new ServiceBusJmsConnectionFactory(CONNECTION_STRING, settings(ServiceBusJmsPricingTier.STANDARD));
        ServiceBusJmsConnectionFactory premium =
            new ServiceBusJmsConnectionFactory("example", "fake", "second.servicebus.windows.net",
                settings(ServiceBusJmsPricingTier.PREMIUM));
        assertFalse(properties(standard).containsKey(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
        assertEquals(Boolean.TRUE, properties(premium).get(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
        assertFalse(properties(standard).containsKey(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
    }

    @Test
    public void nullTierIsRejectedWithoutChangingTheCurrentValue() {
        ServiceBusJmsConnectionFactorySettings settings = settings(ServiceBusJmsPricingTier.STANDARD);
        assertThrows(NullPointerException.class, () -> settings.setPricingTier(null));
        assertEquals(ServiceBusJmsPricingTier.STANDARD, settings.getPricingTier());
    }

    @Test
    public void jndiRoundTripPreservesBothTiers() throws Exception {
        for (ServiceBusJmsPricingTier tier : ServiceBusJmsPricingTier.values()) {
            ServiceBusJmsConnectionFactory original = new ServiceBusJmsConnectionFactory(CONNECTION_STRING, settings(tier));
            Reference reference = original.getReference();
            assertEquals(tier.name(), reference.get("pricingTier").getContent());
            ServiceBusJmsConnectionFactory restored = restore(reference);
            assertEquals(tier, restored.getSettings().getPricingTier());
            assertEquals(tier == ServiceBusJmsPricingTier.PREMIUM,
                properties(restored).containsKey(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
        }
    }

    @Test
    public void legacyJndiReferenceRetainsPremiumDefault() throws Exception {
        Reference reference = new ServiceBusJmsConnectionFactory(CONNECTION_STRING, null).getReference();
        for (int index = 0; index < reference.size(); index++) {
            if ("pricingTier".equals(reference.get(index).getType())) {
                reference.remove(index);
                break;
            }
        }
        ServiceBusJmsConnectionFactory restored = restore(reference);
        assertEquals(ServiceBusJmsPricingTier.PREMIUM, restored.getSettings().getPricingTier());
        assertEquals(Boolean.TRUE, properties(restored).get(ServiceBusJmsConnectionFactorySettings.IsClientProvider));
    }

    @Test
    public void invalidJndiTierIsRejected() {
        ServiceBusJmsConnectionFactory factory = new ServiceBusJmsConnectionFactory();
        Map<String, String> properties = new HashMap<>();
        properties.put("connectionString", CONNECTION_STRING);
        for (String invalid : new String[] { "", "BASIC", "standard", null }) {
            properties.put("pricingTier", invalid);
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> factory.setProperties(properties));
            assertTrue(error.getMessage().contains("STANDARD or PREMIUM"));
        }
    }

    private static ServiceBusJmsConnectionFactorySettings settings(ServiceBusJmsPricingTier tier) {
        ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings();
        settings.setPricingTier(tier);
        settings.setQueueAutoDeleteOnIdleDurationInSeconds(300);
        settings.setTopicAutoDeleteOnIdleDurationInSeconds(400);
        settings.setSubscriberAutoDeleteOnIdleDurationInSeconds(500);
        return settings;
    }

    private static ServiceBusJmsConnectionFactory restore(Reference reference) throws Exception {
        return (ServiceBusJmsConnectionFactory) new JNDIReferenceFactory()
            .getObjectInstance(reference, null, null, null);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> properties(ServiceBusJmsConnectionFactory factory) throws Exception {
        JmsConnectionFactory inner = (JmsConnectionFactory) factory.getConectionFactory();
        Field extensionMap = JmsConnectionFactory.class.getDeclaredField("extensionMap");
        extensionMap.setAccessible(true);
        Map<JmsConnectionExtensions, BiFunction<Connection, URI, Object>> extensions =
            (Map<JmsConnectionExtensions, BiFunction<Connection, URI, Object>>) extensionMap.get(inner);
        return new HashMap<>((Map<String, Object>) extensions.get(JmsConnectionExtensions.AMQP_OPEN_PROPERTIES)
            .apply(null, null));
    }
}
