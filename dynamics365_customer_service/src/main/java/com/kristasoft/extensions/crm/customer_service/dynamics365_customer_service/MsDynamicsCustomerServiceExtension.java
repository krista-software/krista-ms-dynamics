/*
 * Ms Dynamics Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.*;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.stores.MsDynamicsCustomerServiceAttributesStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@Field.Text(value = MsDynamicsCustomerServiceAttributes.CLIENT_ID)
@Field.Text(value = MsDynamicsCustomerServiceAttributes.CLIENT_SECRET, isSecured = true)
@Field.Text(value = MsDynamicsCustomerServiceAttributes.TENANT_ID, isSecured = true)
@Field.Text(value = MsDynamicsCustomerServiceAttributes.BASE_URL)
@Field.Text(value = MsDynamicsCustomerServiceAttributes.EMAIL)
@Extension(version = "2.0.7", jaxrsId = MsDynamicsCustomerServiceExtension.JAXRS_ID, name = "Ms Dynamics 365 Customer Service")
@StaticResource(path = "docs", file = "docs")
@Java(version = Java.Version.JAVA_21)
public final class MsDynamicsCustomerServiceExtension {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsDynamicsCustomerServiceExtension.class);
    static final String JAXRS_ID = "dynamicscrm";
    private final MsDynamicsCustomerServiceAttributes customerServiceAttributes;
    private final RestClientProviderFactory clientProviderFactory;
    private final MsDynamicsRequestAuthenticator requestAuthenticator;
    private final Invoker invoker;

    @Inject
    public MsDynamicsCustomerServiceExtension(MsDynamicsCustomerServiceAttributes customerServiceAttributes,
                                              RestClientProviderFactory clientProviderFactory,
                                              MsDynamicsCustomerServiceAttributesStore customerServiceAttributesStore,
                                              Invoker invoker) {
        this(customerServiceAttributes,
                clientProviderFactory,
                new MsDynamicsRequestAuthenticator(customerServiceAttributesStore, customerServiceAttributes),
                invoker);
    }

    public MsDynamicsCustomerServiceExtension(MsDynamicsCustomerServiceAttributes customerServiceAttributes,
                                              RestClientProviderFactory clientProviderFactory,
                                              MsDynamicsRequestAuthenticator requestAuthenticator,
                                              Invoker invoker) {
        this.customerServiceAttributes = customerServiceAttributes;
        this.clientProviderFactory = clientProviderFactory;
        this.requestAuthenticator = requestAuthenticator;
        this.invoker = invoker;
    }

    @InvokerRequest(InvokerRequest.Type.AUTHENTICATOR)
    public RequestAuthenticator getRequestAuthenticator() {
        return requestAuthenticator;
    }

    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of("Documentation", "static/docs");
    }

    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateConnection(Map<String, Object> connectionAttributes) throws IOException {
        LOGGER.info("Validating connection attributes: {}", connectionAttributes);
        MsDynamicsCustomerServiceAttributes attributes = MsDynamicsCustomerServiceAttributes
                .create(connectionAttributes, invoker);
        if (attributes.getMailId() != null && !attributes.getMailId().isBlank()) {
            validateAndTestConnection(clientProviderFactory.create(attributes));
        }
    }

    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() throws IOException {
        validateAndTestConnection(clientProviderFactory.create(customerServiceAttributes));
    }

    private void validateAndTestConnection(RestClientProvider restClientProvider) {
        LOGGER.info("Validating and testing connection");
        try {
            LOGGER.debug("Calling WhoAmI endpoint to test connection");
            String response = restClientProvider.getRestClientForAdmin().get("WhoAmI", new HashMap<>());
            LOGGER.info("Connection test successful. Response received from WhoAmI endpoint");
            LOGGER.debug("WhoAmI response: {}", response);
        } catch (MustAuthorizeException cause) {
            LOGGER.error("MustAuthenticateException during connection test - Message: {} | Cause: {} | StackTrace: {}",
                    cause.getMessage(), cause.getClass().getSimpleName(), cause.getStackTrace());
            throw cause;
        } catch (MalformedURLException cause) {
            LOGGER.error("MalformedURLException during connection test - Message: {} | Cause: {} | StackTrace: {}",
                    cause.getMessage(), cause.getClass().getSimpleName(), cause.getStackTrace());
            throw new RuntimeException("Malformed URL: " + cause.getMessage(), cause.getCause());
        } catch (Exception cause) {
            LOGGER.error("Unexpected error during connection test - Message: {} | Cause: {} | StackTrace: {}",
                    cause.getMessage(), cause.getClass().getSimpleName(), cause.getStackTrace());
            throw new RuntimeException("Error occurred during connection test: " + cause.getMessage(), cause.getCause());
        }
    }

    @InvokerRequest(InvokerRequest.Type.INVOKER_UPDATED)
    public void attributesUpdated(Map<String, Object> oldAttributes, Map<String, Object> newAttributes) {
        customerServiceAttributes.update(newAttributes);
    }
}
