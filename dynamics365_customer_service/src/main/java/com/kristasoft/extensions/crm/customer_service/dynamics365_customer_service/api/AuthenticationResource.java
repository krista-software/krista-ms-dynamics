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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.api;

import app.krista.ksdk.authentication.AuthorizationListener;
import app.krista.ksdk.context.AuthorizationContext;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.MsDynamicsCustomerServiceAttributes;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.stores.MsDynamicsCustomerServiceAttributesStore;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.stores.RefreshTokenStore;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants.INVALID_STATE_PARAMETERS;

@Path("/")
public final class AuthenticationResource {
    public static final String USER_AUTHENTICATED_SUCCESSFULLY = "User authenticated successfully.";
    public static final String SAVE_THE_CHANGES = "Save the changes.";
    private static final String CODE = "code";
    private static final String STATE = "state";
    private final RefreshTokenStore refreshTokenStore;
    private final MsDynamicsCustomerServiceAttributesStore attributeStore;
    private final RestClientProviderFactory providerFactory;
    private final AuthorizationContext context;
    private final AuthorizationListener authorizationListener;

    @Inject
    public AuthenticationResource(RestClientProviderFactory providerFactory,
                                  MsDynamicsCustomerServiceAttributesStore attributeStore,
                                  RefreshTokenStore refreshTokenStore, AuthorizationContext context,
                                  AuthorizationListener authorizationListener) {

        this.refreshTokenStore = refreshTokenStore;
        this.attributeStore = attributeStore;
        this.providerFactory = providerFactory;
        this.authorizationListener = authorizationListener;
        this.context = context;
    }

    @GET
    @Path("callback")
    public String getCallBack(@QueryParam(CODE) String code, @QueryParam(STATE) String state) throws IOException {
        Objects.requireNonNull(code);
        String[] parts = state.split("#");
        if (parts[0].isBlank() || parts.length > 2) {
            throw new BadRequestException(INVALID_STATE_PARAMETERS);
        }
        String key = parts[0];
        String authContextId = parts.length == 2 ? parts[1] : null;
        try {
            RestClientProvider clientProvider;
            if (authContextId == null) {
                clientProvider = providerFactory.create();
            } else {
                MsDynamicsCustomerServiceAttributes effectiveAttributes = attributeStore.load(authContextId);
                clientProvider = providerFactory.create(effectiveAttributes);
            }
            OAuth2AccessToken accessToken = clientProvider.getCustomerServiceAttributes().getOAuth20Service()
                    .getAccessToken(code);
            key = key + "#" + clientProvider.getCustomerServiceAttributes().getClientId() +
                    clientProvider.getCustomerServiceAttributes().getClientSecret();
            refreshTokenStore.put(key, accessToken.getRefreshToken());
            if (context.isAuthenticated()) {
                authorizationListener.authorized();
                return USER_AUTHENTICATED_SUCCESSFULLY;
            }
            return USER_AUTHENTICATED_SUCCESSFULLY + SAVE_THE_CHANGES;

        } catch (OAuthException cause) {
            throw new IllegalStateException(getErrorDescription(cause), cause.getCause());
        } catch (IOException | ExecutionException cause) {
            throw new IllegalStateException("Error occurred during authorization ", cause.getCause());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Error occurred during authorization ", interruptedException.getCause());
        } finally {
            if (authContextId != null) {
                attributeStore.remove(authContextId);
            }
        }
    }

    private String getErrorDescription(OAuthException cause) {
        String errorDescription = "Authorization failed. ";
        if (cause.getMessage() != null && !cause.getMessage().isBlank()) {
            var exceptionMap = Constants.GSON.fromJson(cause.getMessage(), Map.class);
            if (exceptionMap.containsKey("error_description")) {
                errorDescription = errorDescription + exceptionMap.get("error_description");
            }
        }
        return errorDescription;
    }
}




