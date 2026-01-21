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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl;


import app.krista.extension.authorization.MustAuthorizeException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

public class RestClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);

    private static final Gson gson = new Gson();
    private final WebTarget webTarget;
    private final String accessToken;

    public RestClient(String accessToken, String baseUrl) {
        this.accessToken = accessToken;
        webTarget = ClientBuilder.newClient().target(baseUrl + Constants.API_ENDPOINT);
    }

    /**
     * Handles HTTP response from Dynamics 365 API.
     * Processes successful responses and converts error responses to appropriate exceptions.
     *
     * @param response The JAX-RS Response object from API call
     * @return Response body as string for successful requests
     * @throws IllegalArgumentException if request is malformed (400) or other client/server errors occur
     * @throws MustAuthorizeException if authentication fails (401)
     */
    public static String handleResponse(Response response) {
        String responseString = response.readEntity(String.class);
        LOGGER.info("Response received: {}", responseString);
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            return responseString;
        } else if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            throw new IllegalArgumentException(response.getStatusInfo().getReasonPhrase() + ". " +
                    "Invalid input parameter detected. Please verify all required fields are provided with valid values. " +
                    "Refer to the extension documentation for field requirements and formats.");
        } else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
            throw new MustAuthorizeException(response.getStatusInfo().getReasonPhrase());
        } else throw new IllegalArgumentException(gson
                .fromJson(responseString, JsonObject.class)
                .getAsJsonObject("error")
                .get("message").getAsString());
    }

    /**
     * Executes HTTP GET request to Dynamics 365 API with query parameters.
     * Automatically adds Bearer token authentication header.
     *
     * @param url The API endpoint path (relative to base URL)
     * @param queryParams Map of OData query parameters (e.g., $filter, $select, $orderby)
     * @return Response body as JSON string
     * @throws IllegalArgumentException if request fails with client/server error
     * @throws MustAuthorizeException if authentication fails
     */
    public String get(String url, Map<String, Object> queryParams) {
        WebTarget resourceTarget = webTarget.path(url);
        for (Map.Entry<String, Object> entrySet : queryParams.entrySet()) {
            resourceTarget = resourceTarget.queryParam(entrySet.getKey(), entrySet.getValue());
        }
        Invocation.Builder request = resourceTarget.request(MediaType.WILDCARD);
        addBearerTokenToHeader(request);
        request.header("Content-Type", "text/plain");
        return handleResponse(request.get());
    }

    /**
     * Executes HTTP POST request to Dynamics 365 API to create a new entity.
     * Automatically adds Bearer token authentication and "Prefer: return=representation" header.
     *
     * @param url The API endpoint path (relative to base URL)
     * @param body JSONObject containing entity data to create
     * @return Response body as JSON string containing created entity with server-generated fields
     * @throws IllegalArgumentException if request fails with client/server error
     * @throws MustAuthorizeException if authentication fails
     */
    public String post(String url, JSONObject body) {
        Invocation.Builder request = webTarget.path(url).request(MediaType.APPLICATION_JSON);
        addBearerTokenToHeader(request);
        request.header("Prefer", "return=representation");
        return handleResponse(request.post(Entity.entity(body.toString(), MediaType.APPLICATION_JSON)));
    }

    /**
     * Executes HTTP PATCH request to Dynamics 365 API to update an existing entity.
     * Automatically adds Bearer token authentication and "Prefer: return=representation" header.
     * Uses method workaround for PATCH support.
     *
     * @param url The API endpoint path (relative to base URL)
     * @param body JSON string containing fields to update
     * @return Response body as JSON string containing updated entity
     * @throws IllegalArgumentException if request fails with client/server error
     * @throws MustAuthorizeException if authentication fails
     */
    public String patch(String url, String body) {
        webTarget.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        Invocation.Builder request = webTarget.path(url).request(MediaType.APPLICATION_JSON);
        addBearerTokenToHeader(request);
        request.header("Content-Type", MediaType.APPLICATION_JSON);
        request.header("Prefer", "return=representation");
        return handleResponse(request.method(HttpMethod.PATCH, Entity.entity(body, MediaType.APPLICATION_JSON)));
    }

    /**
     * Executes HTTP DELETE request to Dynamics 365 API to delete an entity.
     * Automatically adds Bearer token authentication header.
     *
     * @param url The API endpoint path (relative to base URL)
     * @throws IllegalArgumentException if request fails with client/server error
     * @throws MustAuthorizeException if authentication fails
     */
    public void delete(String url) {
        Invocation.Builder request = webTarget.path(url).request();
        addBearerTokenToHeader(request);
        handleResponse(request.delete());
    }

    /**
     * Adds Bearer token authentication header to the request.
     *
     * @param request The JAX-RS Invocation.Builder to add header to
     */
    private void addBearerTokenToHeader(Invocation.Builder request) {
        request.header("Authorization", "Bearer " + accessToken);
    }
}
