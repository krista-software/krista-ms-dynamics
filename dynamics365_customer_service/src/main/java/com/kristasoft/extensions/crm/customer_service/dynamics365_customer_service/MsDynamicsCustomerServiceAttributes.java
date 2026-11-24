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

import app.krista.extension.executor.Invoker;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import app.krista.extension.util.InvokerAttributes;
import com.github.scribejava.apis.MicrosoftAzureActiveDirectory20Api;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

@Service
public class MsDynamicsCustomerServiceAttributes {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsDynamicsCustomerServiceAttributes.class);

    public static final String BASE_URL = "baseUrl";
    public static final String CLIENT_ID = "clientId";
    public static final String TENANT_ID = "tenantId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String EMAIL = "email";
    private String routingUrl;
    private String clientId;
    private String clientSecret;
    private String baseUrl;
    private String tenantId;
    private String mailId;
    private OAuth20Service oAuth20Service;


    @Inject
    public MsDynamicsCustomerServiceAttributes(Invoker invoker) {
        this(invoker.getAttributes(), invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME,
                RoutingInfo.Type.APPLIANCE));
    }

    public MsDynamicsCustomerServiceAttributes(Map<String, Object> attributes, String routingURL) {
        this(InvokerAttributes.getStringOrNull(attributes, CLIENT_ID),
                InvokerAttributes.getStringOrNull(attributes, CLIENT_SECRET),
                InvokerAttributes.getStringOrNull(attributes, BASE_URL),
                InvokerAttributes.getStringOrNull(attributes, EMAIL),
                InvokerAttributes.getStringOrNull(attributes, TENANT_ID),
                routingURL
        );
    }

    public MsDynamicsCustomerServiceAttributes(String clientId, String clientSecret, String baseUrl, String mailId,
                                               String tenantId, String routingUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
        this.mailId = mailId;
        this.tenantId = tenantId;
        this.routingUrl = routingUrl;
    }

    public static MsDynamicsCustomerServiceAttributes create(Map<String, Object> attributes, Invoker invoker) {
        LOGGER.info("Creating attributes: {}", attributes);
        return new MsDynamicsCustomerServiceAttributes(attributes,
                invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME, RoutingInfo.Type.APPLIANCE));
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public String getTenantId() {
        return tenantId;
    }


    public String getMailId() {
        return mailId;
    }

    public String getCallbackUrl() {
        return routingUrl + "/dynamicscrm/callback";
    }

    public void update(Map<String, Object> newAttributes) {
        clientId = InvokerAttributes.getStringOrNull(newAttributes, CLIENT_ID);
        clientSecret = InvokerAttributes.getStringOrNull(newAttributes, CLIENT_SECRET);
        baseUrl = InvokerAttributes.getStringOrNull(newAttributes, BASE_URL);
        mailId = InvokerAttributes.getStringOrNull(newAttributes, EMAIL);
        tenantId = InvokerAttributes.getStringOrNull(newAttributes, TENANT_ID);
    }

    public OAuth20Service getOAuth20Service() {
        if (oAuth20Service == null) {
            oAuth20Service = new ServiceBuilder(getClientId())
                    .apiSecret(getClientSecret())
                    .callback(getCallbackUrl())
                    .defaultScope(Constants.REQUIRED_SCOPE)
                    .build(MicrosoftAzureActiveDirectory20Api.custom(getTenantId()));
        }
        return oAuth20Service;
    }
}
