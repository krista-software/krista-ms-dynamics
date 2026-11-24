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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors;

import app.krista.ksdk.context.AuthorizationContext;
import app.krista.ksdk.context.RequestContext;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.MsDynamicsCustomerServiceAttributes;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.stores.MsDynamicsCustomerServiceAttributesStore;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.stores.RefreshTokenStore;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

@Service
public class RestClientProviderFactory {
    private final RefreshTokenStore refreshTokenStore;
    private final AuthorizationContext authorizationContext;
    private final RequestContext requestContext;
    private final MsDynamicsCustomerServiceAttributesStore customerServiceAttributesStore;
    private final RestClientProvider defaultClientProvider;

    @Inject
    public RestClientProviderFactory(RefreshTokenStore refreshTokenStore,
                                     MsDynamicsCustomerServiceAttributesStore customerServiceAttributesStore,
                                     MsDynamicsCustomerServiceAttributes customerServiceAttributes,
                                     RequestContext requestContext, AuthorizationContext authorizationContext) {
        this(refreshTokenStore, customerServiceAttributesStore, requestContext, authorizationContext,
                new RestClientProvider(customerServiceAttributes, refreshTokenStore, authorizationContext,
                        requestContext, null));
    }

    public RestClientProviderFactory(RefreshTokenStore refreshTokenStore,
                                     MsDynamicsCustomerServiceAttributesStore customerServiceAttributesStore,
                                     RequestContext requestContext, AuthorizationContext authorizationContext,
                                     RestClientProvider defaultClientProvider) {
        this.refreshTokenStore = refreshTokenStore;
        this.customerServiceAttributesStore = customerServiceAttributesStore;
        this.requestContext = requestContext;
        this.authorizationContext = authorizationContext;
        this.defaultClientProvider = defaultClientProvider;
    }

    public RestClientProvider create(MsDynamicsCustomerServiceAttributes customerServiceAttributes) {
        String authContextId = customerServiceAttributesStore.save(customerServiceAttributes);
        return new RestClientProvider(customerServiceAttributes, refreshTokenStore, authorizationContext,
                requestContext, authContextId);
    }

    public RestClientProvider create() {
        return defaultClientProvider;
    }
}
