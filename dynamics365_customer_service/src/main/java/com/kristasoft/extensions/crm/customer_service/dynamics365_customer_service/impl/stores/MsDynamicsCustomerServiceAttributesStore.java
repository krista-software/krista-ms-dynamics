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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.stores;

import app.krista.extension.executor.Invoker;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import app.krista.extensions.util.KeyValueStore;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.MsDynamicsCustomerServiceAttributes;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

@Service
public class MsDynamicsCustomerServiceAttributesStore {

    private final Invoker invoker;
    private final KeyValueStore store;

    @Inject
    public MsDynamicsCustomerServiceAttributesStore(Invoker invoker, KeyValueStore store) {
        this.invoker = invoker;
        this.store = store;
    }

    public MsDynamicsCustomerServiceAttributes load(String authContextId) {
        var attributes = Constants.GSON.fromJson(String.valueOf(store.get(authContextId)), Map.class);
        return new MsDynamicsCustomerServiceAttributes(
                (String) attributes.get(MsDynamicsCustomerServiceAttributes.CLIENT_ID),
                (String) attributes.get(MsDynamicsCustomerServiceAttributes.CLIENT_SECRET),
                (String) attributes.get(MsDynamicsCustomerServiceAttributes.BASE_URL),
                (String) attributes.get(MsDynamicsCustomerServiceAttributes.EMAIL),
                (String) attributes.get(MsDynamicsCustomerServiceAttributes.TENANT_ID),
                invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME, RoutingInfo.Type.APPLIANCE));
    }

    public String save(MsDynamicsCustomerServiceAttributes customerServiceAttributes) {
        String authContextId = UUID.randomUUID().toString();
        store.put(authContextId, Constants.GSON.toJson(Map.of(
                MsDynamicsCustomerServiceAttributes.CLIENT_ID, customerServiceAttributes.getClientId(),
                MsDynamicsCustomerServiceAttributes.BASE_URL, customerServiceAttributes.getBaseUrl(),
                MsDynamicsCustomerServiceAttributes.CLIENT_SECRET, customerServiceAttributes.getClientSecret(),
                MsDynamicsCustomerServiceAttributes.EMAIL, customerServiceAttributes.getMailId(),
                MsDynamicsCustomerServiceAttributes.TENANT_ID, customerServiceAttributes.getTenantId()))
        );
        return authContextId;
    }

    /**
     * Removes given authContext id from KeyValueStore
     *
     * @param authContextId authContext id
     */
    public void remove(String authContextId) {
        store.remove(authContextId);
    }

}