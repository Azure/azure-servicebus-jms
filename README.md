# Configure the namespace tier

The connection factory defaults to Premium's full JMS provider mode. To use a
Standard namespace, set the tier explicitly before constructing the factory:

```java
import com.azure.servicebus.jms.ServiceBusJmsConnectionFactory;
import com.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;
import com.azure.servicebus.jms.ServiceBusJmsPricingTier;

ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings();
settings.setPricingTier(ServiceBusJmsPricingTier.STANDARD);
ServiceBusJmsConnectionFactory factory =
    new ServiceBusJmsConnectionFactory("<namespace-connection-string>", settings);
```

Use a separate settings instance and connection factory for each namespace.
The setting applies to connections created through the classic Connection/Session
APIs and JMSContext, including queue and topic connection factories. It selects
the AMQP provider mode; it does not change the namespace's tier or the Java API
version. Existing callers that omit the setting retain Premium behavior.

Standard supports only the [documented limited JMS 1.1 subset](https://learn.microsoft.com/azure/service-bus-messaging/service-bus-java-how-to-use-jms-api-amqp).
Pre-create the required entities. Selecting Standard does not enable full JMS
support, temporary destinations, or automatic entity creation. Use Premium for
[full Service Bus JMS support](https://learn.microsoft.com/azure/service-bus-messaging/jms-developer-guide).
Spring's global tier property does not automatically configure manually
constructed factories; set the tier on each factory's settings instead.

JNDI references preserve the selected tier as the `pricingTier` property.
Its accepted values are `STANDARD` and `PREMIUM`; references without the property
retain the Premium default.

# Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
