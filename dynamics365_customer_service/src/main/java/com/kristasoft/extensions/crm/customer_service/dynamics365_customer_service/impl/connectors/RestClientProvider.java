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

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.ksdk.context.RequestContext;
import app.krista.model.field.NamedValuedField;
import com.google.gson.JsonObject;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.MsDynamicsCustomerServiceAttributes;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.RestClient;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.stores.RefreshTokenStore;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.microsoft.aad.msal4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestClientProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientProvider.class);


    public static final String FAILED_TO_GET_ACCOUNT = "Unable to retrieve user account information. Verify the email address is configured correctly in extension attributes.";
    private final Logger logger = LoggerFactory.getLogger(RestClientProvider.class);
    private final RefreshTokenStore refreshTokenStore;
    private final AuthorizationContext authorizationContext;
    private final RequestContext requestContext;
    private final String authContextId;
    private final MsDynamicsCustomerServiceAttributes customerServiceAttributes;


    @Inject
    public RestClientProvider(MsDynamicsCustomerServiceAttributes customerServiceAttributes,
                              RefreshTokenStore refreshTokenStore, AuthorizationContext authorizationContext,
                              RequestContext requestContext) {
        this(customerServiceAttributes, refreshTokenStore, authorizationContext, requestContext, null);
    }

    public RestClientProvider(MsDynamicsCustomerServiceAttributes customerServiceAttributes,
                              RefreshTokenStore refreshTokenStore,
                              AuthorizationContext authorizationContext, RequestContext requestContext,
                              String authContextId) {
        this.customerServiceAttributes = customerServiceAttributes;
        this.refreshTokenStore = refreshTokenStore;
        this.authorizationContext = authorizationContext;
        this.requestContext = requestContext;
        this.authContextId = authContextId;
    }

    public MsDynamicsCustomerServiceAttributes getCustomerServiceAttributes() {
        return customerServiceAttributes;
    }

    /**
     * Gets an authenticated RestClient instance based on request context.
     * Uses service account (email) if not invoked as user, otherwise uses authorized user account.
     *
     * @return RestClient configured with valid access token
     * @throws MalformedURLException if base URL is invalid
     * @throws MustAuthorizeException if no refresh token found or token refresh fails
     */
    public RestClient getRestClient() throws MalformedURLException {
        return getRestClient(!requestContext.invokeAsUser());
    }

    /**
     * Gets an authenticated RestClient instance using service account credentials.
     * Always uses the email configured in extension attributes regardless of request context.
     *
     * @return RestClient configured with valid access token for service account
     * @throws MalformedURLException if base URL is invalid
     * @throws MustAuthorizeException if no refresh token found or token refresh fails
     */
    public RestClient getRestClientForAdmin() throws MalformedURLException {
        return getRestClient(true);
    }

    /**
     * Gets an authenticated RestClient instance with token management.
     * Retrieves refresh token from store, acquires new access token, and creates RestClient.
     *
     * @param useEmail If true, uses service account email; if false, uses authorized user account
     * @return RestClient configured with valid access token
     * @throws MalformedURLException if base URL is invalid
     * @throws MustAuthorizeException if no refresh token found or token refresh fails
     */
    public RestClient getRestClient(boolean useEmail) throws MalformedURLException {
        LOGGER.info("Getting rest client with useEmail: {}", useEmail);
        LOGGER.debug("Request context invokeAsUser: {}", requestContext.invokeAsUser());

        String userId = getUserId(useEmail);
        LOGGER.info("User id: {}", userId);

        String refreshTokenKey = userId + Constants.HASH +
                customerServiceAttributes.getClientId() +
                customerServiceAttributes.getClientSecret();

        String refreshToken = refreshTokenStore.get(refreshTokenKey);

        if (refreshToken == null) {
            LOGGER.info("No refresh token found for userId: {}, throwing MustAuthorizeException", userId);
            throw createMustAuthorizationException(userId, false);
        }

        LOGGER.info("Proceeding to create RestClient for userId: {}", userId);
        return getRestClient(userId, refreshToken);
    }

    /**
     * Determines the user ID to use for authentication.
     * Uses service account email if called from validation or not invoked as user.
     * Otherwise uses the authorized user's account ID.
     *
     * @param calledFromValidateAttributes If true, always uses service account email
     * @return User ID (email or account ID) to use for authentication
     * @throws IllegalStateException if user ID cannot be determined
     */
    private String getUserId(boolean calledFromValidateAttributes) {
        LOGGER.info("Getting user id");
        String userId = calledFromValidateAttributes || !requestContext.invokeAsUser()
                ? customerServiceAttributes.getMailId() : authorizationContext.getAuthorizedAccount().getAccountId();
        if (userId == null) {
            throw new IllegalStateException(FAILED_TO_GET_ACCOUNT);
        }
        return userId;
    }

    /**
     * Creates a MustAuthorizeException with user details for authorization flow.
     * Includes user ID and optional auth context ID in exception details.
     *
     * @param userId The user ID requiring authorization
     * @param reAuthentication If true, indicates token expired; if false, indicates initial authorization needed
     * @return MustAuthorizeException configured with user details
     */
    private MustAuthorizeException createMustAuthorizationException(String userId, boolean reAuthentication) {
        logger.debug(reAuthentication ? Constants.RE_AUTHENTICATION : Constants.AUTHENTICATION);
        List<NamedValuedField> details = new ArrayList<>();
        NamedValuedField userIdField = new NamedValuedField(Constants.USER_ID, Constants.TEXT, userId, new HashMap<>(),
                new HashMap<>());
        details.add(userIdField);
        if (authContextId != null) {
            NamedValuedField contextIdField = new NamedValuedField(Constants.AUTH_CONTEXT_ID, Constants.TEXT,
                    authContextId, new HashMap<>(), new HashMap<>());
            details.add(contextIdField);
        }
        return new MustAuthorizeException(reAuthentication ? "Your authentication token has expired. Please re-authorize to continue."
                : Constants.AUTHORIZATION_PROMPT, details);
    }

    /**
     * Creates an authenticated RestClient using MSAL4J to acquire access token.
     * Uses refresh token to obtain new access token, updates refresh token in store, and creates RestClient.
     *
     * @param userId The user ID (email or account ID) for authentication
     * @param refreshToken The refresh token to use for acquiring access token
     * @return RestClient configured with valid access token
     * @throws MalformedURLException if base URL is invalid
     * @throws MustAuthorizeException if token acquisition fails (requires re-authorization)
     */
    private RestClient getRestClient(String userId, String refreshToken) throws MalformedURLException {
        LOGGER.info("Creating RestClient for userId: {}", userId);
        try {
            RefreshTokenParameters refreshTokenParameters = RefreshTokenParameters
                    .builder(getScopeSet(), refreshToken)
                    .build();
            IClientCredential clientCredential = ClientCredentialFactory
                    .createFromSecret(customerServiceAttributes.getClientSecret());
            ConfidentialClientApplication confidentialClientApplication = ConfidentialClientApplication
                    .builder(customerServiceAttributes.getClientId(), clientCredential)
                    .authority(getAuthority())
                    .build();
            LOGGER.info("Confidential client application created successfully");
            CompletableFuture<IAuthenticationResult> iAuthenticationResultCompletableFuture = confidentialClientApplication
                    .acquireToken(refreshTokenParameters);
            IAuthenticationResult authenticationResult = iAuthenticationResultCompletableFuture.get();
            final String cachedTokenContent = confidentialClientApplication.tokenCache().serialize();
            updateRefreshToken(userId, Constants.GSON.fromJson(cachedTokenContent, JsonObject.class));

            LOGGER.info("RestClient created successfully for userId: {} with baseUrl: {}", userId, customerServiceAttributes.getBaseUrl());
            return new RestClient(authenticationResult.accessToken(), customerServiceAttributes.getBaseUrl());
        } catch (ExecutionException cause) {
            LOGGER.error("ExecutionException occurred while acquiring token for userId: {}", userId, cause);
            throw createMustAuthorizationException(userId, true);
        } catch (InterruptedException interruptedException) {
            LOGGER.error("InterruptedException occurred while acquiring token for userId: {}", userId, interruptedException);
            Thread.currentThread().interrupt();
            throw createMustAuthorizationException(userId, true);
        }
    }

    /**
     * Updates the refresh token in the store after successful token acquisition.
     * Extracts the new refresh token from MSAL4J token cache and stores it.
     *
     * @param userId The user ID to associate with the refresh token
     * @param jsonTokenCache The serialized token cache from MSAL4J containing refresh token
     */
    private void updateRefreshToken(String userId, JsonObject jsonTokenCache) {
        JsonObject refreshToken = jsonTokenCache.get("RefreshToken").getAsJsonObject();

        String firstKey = refreshToken.keySet().iterator().next();

        JsonObject tokenData = refreshToken.getAsJsonObject(firstKey);

        String secret = tokenData.get("secret").getAsString();
        refreshTokenStore.put(userId, secret);
        LOGGER.info("Refresh token updated successfully for userId: {}", userId);
    }

    /**
     * Builds the OAuth 2.0 scope set for Dynamics 365 API access.
     * Includes base URL with default scope and offline_access for refresh token.
     *
     * @return Set of scope strings for token acquisition
     */
    @NotNull
    private Set<String> getScopeSet() {
        Set<String> scopeSet = new HashSet<>();
        String resource = customerServiceAttributes.getBaseUrl();
        scopeSet.add(resource + Constants.DEFAULT_SCOPE);
        scopeSet.add(Constants.OFFLINE_ACCESS);
        return scopeSet;
    }

    /**
     * Constructs the Microsoft Entra ID (Azure AD) authority URL for the tenant.
     *
     * @return Authority URL in format: https://login.microsoftonline.com/{tenantId}
     */
    private String getAuthority() {
        String tenantId = customerServiceAttributes.getTenantId();
        String authority = Constants.AUTHORITY + tenantId;
        LOGGER.debug("Using tenant-specific authority: {}", authority);
        return authority;
    }
}
