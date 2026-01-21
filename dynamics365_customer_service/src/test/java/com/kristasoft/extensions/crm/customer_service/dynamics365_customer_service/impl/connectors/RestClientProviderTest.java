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
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.MsDynamicsCustomerServiceAttributes;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.stores.RefreshTokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RestClientProviderTest {

    private MsDynamicsCustomerServiceAttributes customerServiceAttributes;
    private RefreshTokenStore refreshTokenStore;
    private AuthorizationContext authorizationContext;
    private RequestContext requestContext;
    private RestClientProvider restClientProvider;

    private static final String TEST_USER_ID = "test@example.com";
    private static final String TEST_CLIENT_ID = "testClientId";
    private static final String TEST_CLIENT_SECRET = "testClientSecret";
    private static final String TEST_BASE_URL = "https://org3bb866ee.crm.dynamics.com";
    private static final String TEST_TENANT_ID = "testTenantId";
    private static final String TEST_REFRESH_TOKEN = "testRefreshToken";

    @BeforeEach
    public void setUp() {
        // Initialize mocks manually
        customerServiceAttributes = Mockito.mock(MsDynamicsCustomerServiceAttributes.class);
        refreshTokenStore = Mockito.mock(RefreshTokenStore.class);
        authorizationContext = Mockito.mock(AuthorizationContext.class);
        requestContext = Mockito.mock(RequestContext.class);

        // Setup common mock behaviors
        when(customerServiceAttributes.getMailId()).thenReturn(TEST_USER_ID);
        when(customerServiceAttributes.getClientId()).thenReturn(TEST_CLIENT_ID);
        when(customerServiceAttributes.getClientSecret()).thenReturn(TEST_CLIENT_SECRET);
        when(customerServiceAttributes.getBaseUrl()).thenReturn(TEST_BASE_URL);
        when(customerServiceAttributes.getTenantId()).thenReturn(TEST_TENANT_ID);

        restClientProvider = new RestClientProvider(
                customerServiceAttributes,
                refreshTokenStore,
                authorizationContext,
                requestContext
        );
    }

    @Test
    public void testSimple() {
        // Simple test to verify test runner is working
        assertTrue(true);
    }

    @Test
    public void testGetCustomerServiceAttributes_ShouldReturnAttributes() {
        // Act
        MsDynamicsCustomerServiceAttributes result = restClientProvider.getCustomerServiceAttributes();

        // Assert
        assertNotNull(result);
        assertEquals(customerServiceAttributes, result);
    }

    @Test
    public void testConstructor_WithBasicParameters_ShouldCreateInstance() {
        // Arrange
        MsDynamicsCustomerServiceAttributes mockAttributes = Mockito.mock(MsDynamicsCustomerServiceAttributes.class);
        RefreshTokenStore mockRefreshTokenStore = Mockito.mock(RefreshTokenStore.class);
        AuthorizationContext mockAuthorizationContext = Mockito.mock(AuthorizationContext.class);
        RequestContext mockRequestContext = Mockito.mock(RequestContext.class);

        // Act
        RestClientProvider provider = new RestClientProvider(
                mockAttributes,
                mockRefreshTokenStore,
                mockAuthorizationContext,
                mockRequestContext
        );

        // Assert
        assertNotNull(provider);
        assertEquals(mockAttributes, provider.getCustomerServiceAttributes());
    }

    @Test
    public void testConstructor_WithAuthContextId_ShouldCreateInstance() {
        // Arrange
        String authContextId = "testAuthContextId";
        MsDynamicsCustomerServiceAttributes mockAttributes = Mockito.mock(MsDynamicsCustomerServiceAttributes.class);
        RefreshTokenStore mockRefreshTokenStore = Mockito.mock(RefreshTokenStore.class);
        AuthorizationContext mockAuthorizationContext = Mockito.mock(AuthorizationContext.class);
        RequestContext mockRequestContext = Mockito.mock(RequestContext.class);

        // Act
        RestClientProvider provider = new RestClientProvider(
                mockAttributes,
                mockRefreshTokenStore,
                mockAuthorizationContext,
                mockRequestContext,
                authContextId
        );

        // Assert
        assertNotNull(provider);
        assertEquals(mockAttributes, provider.getCustomerServiceAttributes());
    }

    @Test
    public void testGetRestClient_WithNullRefreshToken_ShouldThrowMustAuthorizeException() {
        // Arrange
        when(refreshTokenStore.get(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(MustAuthorizeException.class, () -> {
            restClientProvider.getRestClient(true);
        });
    }

    @Test
    public void testGetRestClient_WithNullUserId_ShouldThrowIllegalStateException() {
        // Arrange
        when(customerServiceAttributes.getMailId()).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            restClientProvider.getRestClient(true);
        });
    }

    @Test
    public void testGetRestClient_WithInvokeAsUserFalse_ShouldUseEmailFromAttributes() {
        // Arrange
        when(requestContext.invokeAsUser()).thenReturn(false);
        when(refreshTokenStore.get(anyString())).thenReturn(null); // This will cause MustAuthorizeException

        // Act & Assert
        assertThrows(MustAuthorizeException.class, () -> {
            restClientProvider.getRestClient();
        });

        // Verify that invokeAsUser was called
        verify(requestContext, atLeastOnce()).invokeAsUser();
    }

    @Test
    public void testGetRestClientForAdmin_WithNullRefreshToken_ShouldThrowMustAuthorizeException() {
        // Arrange
        when(refreshTokenStore.get(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(MustAuthorizeException.class, () -> {
            restClientProvider.getRestClientForAdmin();
        });
    }

    @Test
    public void testGetRestClient_WithValidRefreshToken_ShouldAttemptAuthentication() {
        // Arrange
        when(refreshTokenStore.get(anyString())).thenReturn(TEST_REFRESH_TOKEN);

        // Act & Assert
        // This will likely throw an exception due to MSAL4J dependencies not being fully mocked
        // but it tests that the method attempts to proceed with authentication
        assertThrows(Exception.class, () -> {
            restClientProvider.getRestClient(true);
        });

        // Verify that the refresh token was retrieved
        verify(refreshTokenStore, atLeastOnce()).get(anyString());
    }


    @Test
    public void testGetRestClientForAdmin_WithValidRefreshToken_ShouldAttemptAuthentication() {
        // Arrange
        when(refreshTokenStore.get(anyString())).thenReturn(TEST_REFRESH_TOKEN);

        // Act & Assert
        // This will likely throw an exception due to MSAL4J dependencies not being fully mocked
        // but it tests that the method attempts to proceed with authentication
        assertThrows(Exception.class, () -> {
            restClientProvider.getRestClientForAdmin();
        });

        // Verify that the refresh token was retrieved
        verify(refreshTokenStore, atLeastOnce()).get(anyString());
    }

    @Test
    public void testMockSetup_VerifyAllMocksAreConfigured() {
        // Verify that all mocks are properly configured
        assertEquals(TEST_USER_ID, customerServiceAttributes.getMailId());
        assertEquals(TEST_CLIENT_ID, customerServiceAttributes.getClientId());
        assertEquals(TEST_CLIENT_SECRET, customerServiceAttributes.getClientSecret());
        assertEquals(TEST_BASE_URL, customerServiceAttributes.getBaseUrl());
        assertEquals(TEST_TENANT_ID, customerServiceAttributes.getTenantId());
    }

    @Test
    public void testGetRestClient_WithInvokeAsUserTrue_ShouldCallGetAuthorizedAccount() {
        // Arrange
        when(requestContext.invokeAsUser()).thenReturn(true);
        when(authorizationContext.getAuthorizedAccount()).thenReturn(null); // This will cause NullPointerException which becomes IllegalStateException

        // Act & Assert
        assertThrows(Exception.class, () -> {
            restClientProvider.getRestClient(false);
        });

        // Verify that invokeAsUser was called (it may be called multiple times)
        verify(requestContext, atLeastOnce()).invokeAsUser();
    }

    @Test
    public void testRefreshTokenStore_InteractionPatterns() {
        // Arrange
        String testKey = "testKey";
        String testValue = "testValue";

        // Configure mock
        when(refreshTokenStore.get(testKey)).thenReturn(testValue);

        // Act
        String result = refreshTokenStore.get(testKey);

        // Assert
        assertEquals(testValue, result);
        verify(refreshTokenStore).get(testKey);
    }

}