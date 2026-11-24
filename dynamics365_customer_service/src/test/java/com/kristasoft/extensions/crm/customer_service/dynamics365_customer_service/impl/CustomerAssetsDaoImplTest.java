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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.CustomerAsset;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CustomerAssetsDaoImplTest {

    @Mock
    private RestClientProvider mockRestClientProvider;

    @Mock
    private RestClientProviderFactory mockRestClientProviderFactory;

    @Mock
    private RestClient mockRestClient;

    @Mock
    private CaseDaoImpl mockCaseDao;

    private CustomerAssetsDaoImpl customerAssetsDaoImpl;

    private static final String TEST_ACCOUNT_ID = "test-account-123";
    private static final String TEST_CUSTOMER_ASSET_ID = "asset-123";
    private static final String TEST_SYSTEM_TYPE_ID = "system-type-456";
    private static final String TEST_ASSET_NAME = "Test Asset";
    private static final String TEST_STATUS_CODE = "1";
    private static final String TEST_STATE_CODE = "0";
    private static final String TEST_CREATED_ON = "2023-01-01T10:00:00Z";
    private static final String TEST_MODIFIED_ON = "2023-01-02T10:00:00Z";
    private static final String TEST_OWNER_ID = "owner-123";
    private static final String TEST_CS_NUMBER = "CS12345";
    private static final String TEST_ASSET_NUMBER = "AN67890";
    private static final String TEST_SERVICE_PLAN = "service-plan-123";
    private static final String TEST_UPGRADE_TYPE = "Standard";
    private static final String TEST_OOS_CATEGORY = "Maintenance";

    @BeforeEach
    public void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);

        // Setup factory mock
        when(mockRestClientProviderFactory.create()).thenReturn(mockRestClientProvider);
        when(mockRestClientProvider.getRestClient()).thenReturn(mockRestClient);

        // Create instance using provider directly for most tests
        customerAssetsDaoImpl = new CustomerAssetsDaoImpl(mockCaseDao, mockRestClientProvider);
    }

    @Test
    public void testConstructor_WithProviderFactory_ShouldCreateInstance() {
        // Act
        CustomerAssetsDaoImpl dao = new CustomerAssetsDaoImpl(mockRestClientProviderFactory, mockCaseDao);

        // Assert
        assertNotNull(dao);
        verify(mockRestClientProviderFactory).create();
    }

    @Test
    public void testConstructor_WithProvider_ShouldCreateInstance() {
        // Act
        CustomerAssetsDaoImpl dao = new CustomerAssetsDaoImpl(mockCaseDao, mockRestClientProvider);

        // Assert
        assertNotNull(dao);
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithValidAccountId_ShouldReturnCustomerAssets() throws MalformedURLException {
        // Arrange
        String customerAssetsResponse = createMockCustomerAssetsResponse();
        String systemTypeMapResponse = createMockSystemTypeMapResponse();
        String systemTypeDetailsResponse = createMockSystemTypeDetailsResponse();

        // Mock the first call to fetch customer assets data
        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(customerAssetsResponse);

        // Mock the second call to build system type map
        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(systemTypeMapResponse);

        // Mock the system type details call
        when(mockRestClient.get(eq("gua_systemtypes(" + TEST_SYSTEM_TYPE_ID + ")"), any()))
                .thenReturn(systemTypeDetailsResponse);

        // Act
        List<CustomerAsset> result = customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        CustomerAsset asset = result.getFirst();
        assertEquals(TEST_CUSTOMER_ASSET_ID, asset.customerAssetId);
        assertEquals(TEST_ASSET_NAME, asset.name);
        assertEquals(TEST_STATUS_CODE, asset.statusCode);
        assertEquals(TEST_STATE_CODE, asset.stateCode);
        assertEquals(TEST_CREATED_ON, asset.createdOn);
        assertEquals(TEST_MODIFIED_ON, asset.modifiedOn);
        assertEquals(TEST_OWNER_ID, asset.ownerId);
        assertEquals(TEST_ACCOUNT_ID, asset.accountId);
        assertEquals(TEST_CS_NUMBER, asset.cSNumber);
        assertEquals(TEST_ASSET_NUMBER, asset.assetNumber);
        assertTrue(asset.isMonitoredAsset);
        assertEquals(TEST_SERVICE_PLAN, asset.servicePlan);
        assertTrue(asset.isActiveAsset);
        assertEquals(TEST_UPGRADE_TYPE, asset.upgradeType);
        assertEquals(TEST_OOS_CATEGORY, asset.oOSCategory);

        // Verify system type information
        assertEquals("\"" + TEST_SYSTEM_TYPE_ID + "\"", asset.systemTypeId);
        assertEquals("\"Test System Type\"", asset.systemTypeDescription);
        assertEquals(TEST_SYSTEM_TYPE_ID, asset.systemTypeValue);
        assertEquals("\"Test Display Group\"", asset.displayGroup);

        // Verify all API calls were made
        verify(mockRestClient, times(3)).get(anyString(), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithEmptyResponse_ShouldReturnEmptyList() throws MalformedURLException {
        // Arrange
        String emptyResponse = createMockEmptyCustomerAssetsResponse();
        String emptySystemTypeMapResponse = createMockEmptySystemTypeMapResponse();

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(emptyResponse);

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(emptySystemTypeMapResponse);

        // Act
        List<CustomerAsset> result = customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockRestClient, times(2)).get(anyString(), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithNullSystemTypeId_ShouldHandleGracefully() throws MalformedURLException {
        // Arrange
        String customerAssetsResponse = createMockCustomerAssetsResponse();
        String systemTypeMapResponseWithNullSystemType = createMockSystemTypeMapResponseWithNullSystemType();

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(customerAssetsResponse);

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(systemTypeMapResponseWithNullSystemType);

        // Act
        List<CustomerAsset> result = customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        CustomerAsset asset = result.getFirst();
        assertEquals(TEST_CUSTOMER_ASSET_ID, asset.customerAssetId);
        assertEquals(TEST_ASSET_NAME, asset.name);

        // System type information should be empty strings when no system type is found
        assertEquals("", asset.systemTypeId);
        assertEquals("", asset.systemTypeDescription);
        assertEquals("", asset.systemTypeValue);
        assertEquals("", asset.displayGroup);

        verify(mockRestClient, times(2)).get(anyString(), any());
        // Should not call system type details API when system type ID is null
        verify(mockRestClient, never()).get(startsWith("gua_systemtypes("), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithNullCustomerAssetId_ShouldHandleGracefully() throws MalformedURLException {
        // Arrange
        String customerAssetsResponse = createMockCustomerAssetsResponse();
        String systemTypeMapResponseWithNullCustomerId = createMockSystemTypeMapResponseWithNullCustomerId();

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(customerAssetsResponse);

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(systemTypeMapResponseWithNullCustomerId);

        // Act
        List<CustomerAsset> result = customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        CustomerAsset asset = result.getFirst();
        assertEquals(TEST_CUSTOMER_ASSET_ID, asset.customerAssetId);

        // System type information should be empty when customer ID is null in system type map
        assertEquals("", asset.systemTypeId);
        assertEquals("", asset.systemTypeDescription);
        assertEquals("", asset.systemTypeValue);
        assertEquals("", asset.displayGroup);

        verify(mockRestClient, times(2)).get(anyString(), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithNullJsonFields_ShouldHandleNullValues() throws MalformedURLException {
        // Arrange
        String customerAssetsResponseWithNulls = createMockCustomerAssetsResponseWithNullFields();
        String systemTypeMapResponse = createMockSystemTypeMapResponse();
        String systemTypeDetailsResponse = createMockSystemTypeDetailsResponse();

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(customerAssetsResponseWithNulls);

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(systemTypeMapResponse);

        when(mockRestClient.get(eq("gua_systemtypes(" + TEST_SYSTEM_TYPE_ID + ")"), any()))
                .thenReturn(systemTypeDetailsResponse);

        // Act
        List<CustomerAsset> result = customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        CustomerAsset asset = result.getFirst();
        assertEquals(TEST_CUSTOMER_ASSET_ID, asset.customerAssetId);
        assertNull(asset.name); // Should be null when field is null in JSON
        assertNull(asset.statusCode);
        assertNull(asset.stateCode);
        assertFalse(asset.isMonitoredAsset); // Boolean.valueOf(null) returns false
        assertFalse(asset.isActiveAsset);

        verify(mockRestClient, times(3)).get(anyString(), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithRuntimeException_ShouldPropagateException() {
        // Arrange
        when(mockRestClient.get(eq("msdyn_customerassets"), any()))
                .thenThrow(new RuntimeException("API error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);
        });

        assertEquals("API error", exception.getMessage());
        verify(mockRestClient).get(eq("msdyn_customerassets"), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithSystemTypeDetailsRuntimeException_ShouldPropagateException() {
        // Arrange
        String customerAssetsResponse = createMockCustomerAssetsResponse();
        String systemTypeMapResponse = createMockSystemTypeMapResponse();

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(customerAssetsResponse);

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(systemTypeMapResponse);

        when(mockRestClient.get(eq("gua_systemtypes(" + TEST_SYSTEM_TYPE_ID + ")"), any()))
                .thenThrow(new RuntimeException("System type API error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);
        });

        assertEquals("System type API error", exception.getMessage());
        verify(mockRestClient, times(3)).get(anyString(), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithMultipleAssets_ShouldReturnAllAssets() throws MalformedURLException {
        // Arrange
        String multipleAssetsResponse = createMockMultipleCustomerAssetsResponse();
        String systemTypeMapResponse = createMockMultipleSystemTypeMapResponse();
        String systemTypeDetailsResponse1 = createMockSystemTypeDetailsResponse();
        String systemTypeDetailsResponse2 = createMockSystemTypeDetailsResponse2();

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(multipleAssetsResponse);

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(systemTypeMapResponse);

        when(mockRestClient.get(eq("gua_systemtypes(" + TEST_SYSTEM_TYPE_ID + ")"), any()))
                .thenReturn(systemTypeDetailsResponse1);
        when(mockRestClient.get(eq("gua_systemtypes(system-type-789)"), any()))
                .thenReturn(systemTypeDetailsResponse2);

        // Act
        List<CustomerAsset> result = customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        CustomerAsset asset1 = result.getFirst();
        assertEquals(TEST_CUSTOMER_ASSET_ID, asset1.customerAssetId);
        assertEquals(TEST_ASSET_NAME, asset1.name);

        CustomerAsset asset2 = result.get(1);
        assertEquals("asset-456", asset2.customerAssetId);
        assertEquals("Test Asset 2", asset2.name);

        verify(mockRestClient, times(4)).get(anyString(), any()); // 2 main calls + 2 system type details calls
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithBooleanFalseValues_ShouldHandleCorrectly() throws MalformedURLException {
        // Arrange
        String customerAssetsResponseWithFalse = createMockCustomerAssetsResponseWithFalseBooleans();
        String systemTypeMapResponse = createMockSystemTypeMapResponse();
        String systemTypeDetailsResponse = createMockSystemTypeDetailsResponse();

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(customerAssetsResponseWithFalse);

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(systemTypeMapResponse);

        when(mockRestClient.get(eq("gua_systemtypes(" + TEST_SYSTEM_TYPE_ID + ")"), any()))
                .thenReturn(systemTypeDetailsResponse);

        // Act
        List<CustomerAsset> result = customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        CustomerAsset asset = result.getFirst();
        assertEquals(TEST_CUSTOMER_ASSET_ID, asset.customerAssetId);
        assertFalse(asset.isMonitoredAsset); // Should be false
        assertFalse(asset.isActiveAsset); // Should be false

        verify(mockRestClient, times(3)).get(anyString(), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithJsonSyntaxException_ShouldPropagateException() {
        // Arrange
        String invalidJsonResponse = "invalid json";

        when(mockRestClient.get(eq("msdyn_customerassets"), any()))
                .thenReturn(invalidJsonResponse);

        // Act & Assert
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> {
            customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);
        });

        verify(mockRestClient).get(eq("msdyn_customerassets"), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithSystemTypeMapJsonSyntaxException_ShouldPropagateException() {
        // Arrange
        String customerAssetsResponse = createMockCustomerAssetsResponse();
        String invalidSystemTypeMapResponse = "invalid json";

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(customerAssetsResponse);

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(invalidSystemTypeMapResponse);

        // Act & Assert
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> {
            customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);
        });

        verify(mockRestClient, times(2)).get(anyString(), any());
    }

    @Test
    public void testGetCustomerAssetsByAccountId_WithSystemTypeDetailsJsonSyntaxException_ShouldPropagateException() {
        // Arrange
        String customerAssetsResponse = createMockCustomerAssetsResponse();
        String systemTypeMapResponse = createMockSystemTypeMapResponse();
        String invalidSystemTypeDetailsResponse = "invalid json";

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq " + TEST_ACCOUNT_ID))))
                .thenReturn(customerAssetsResponse);

        when(mockRestClient.get(eq("msdyn_customerassets"), argThat(params ->
                params.get("$filter").equals("_msdyn_account_value eq '" + TEST_ACCOUNT_ID + "'") &&
                        params.get("$select").equals("_gua_systemtype_value"))))
                .thenReturn(systemTypeMapResponse);

        when(mockRestClient.get(eq("gua_systemtypes(" + TEST_SYSTEM_TYPE_ID + ")"), any()))
                .thenReturn(invalidSystemTypeDetailsResponse);

        // Act & Assert
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> {
            customerAssetsDaoImpl.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);
        });

        verify(mockRestClient, times(3)).get(anyString(), any());
    }

    // Helper methods for creating mock responses
    private String createMockCustomerAssetsResponse() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();

        JsonObject asset = new JsonObject();
        asset.addProperty("msdyn_customerassetid", TEST_CUSTOMER_ASSET_ID);
        asset.addProperty("msdyn_name", TEST_ASSET_NAME);
        asset.addProperty("statuscode", TEST_STATUS_CODE);
        asset.addProperty("stateCode", TEST_STATE_CODE);
        asset.addProperty("createdon", TEST_CREATED_ON);
        asset.addProperty("modifiedon", TEST_MODIFIED_ON);
        asset.addProperty("_ownerid_value", TEST_OWNER_ID);
        asset.addProperty("_msdyn_account_value", TEST_ACCOUNT_ID);
        asset.addProperty("rsm_csnumber", TEST_CS_NUMBER);
        asset.addProperty("gua_assetnumber", TEST_ASSET_NUMBER);
        asset.addProperty("gua_ismonitoredasset", "true");
        asset.addProperty("gua_inservicedate", "2023-01-01");
        asset.addProperty("gua_lastservicedate", "2023-12-31");
        asset.addProperty("_gua_serviceplan_value", TEST_SERVICE_PLAN);
        asset.addProperty("gua_isactiveasset", "true");
        asset.addProperty("gua_upgradetype", TEST_UPGRADE_TYPE);
        asset.addProperty("gua_ooscategory", TEST_OOS_CATEGORY);

        assetsArray.add(asset);
        response.add("value", assetsArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockEmptyCustomerAssetsResponse() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();
        response.add("value", assetsArray);
        return Constants.GSON.toJson(response);
    }

    private String createMockCustomerAssetsResponseWithNullFields() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();

        JsonObject asset = new JsonObject();
        asset.addProperty("msdyn_customerassetid", TEST_CUSTOMER_ASSET_ID);
        // Add null values for some fields
        asset.add("msdyn_name", null);
        asset.add("statuscode", null);
        asset.add("stateCode", null);
        asset.addProperty("createdon", TEST_CREATED_ON);
        asset.addProperty("modifiedon", TEST_MODIFIED_ON);
        asset.addProperty("_ownerid_value", TEST_OWNER_ID);
        asset.addProperty("_msdyn_account_value", TEST_ACCOUNT_ID);
        asset.addProperty("rsm_csnumber", TEST_CS_NUMBER);
        asset.addProperty("gua_assetnumber", TEST_ASSET_NUMBER);
        asset.add("gua_ismonitoredasset", null); // This will result in false when converted to Boolean
        asset.addProperty("gua_inservicedate", "2023-01-01");
        asset.addProperty("gua_lastservicedate", "2023-12-31");
        asset.addProperty("_gua_serviceplan_value", TEST_SERVICE_PLAN);
        asset.add("gua_isactiveasset", null); // This will result in false when converted to Boolean
        asset.addProperty("gua_upgradetype", TEST_UPGRADE_TYPE);
        asset.addProperty("gua_ooscategory", TEST_OOS_CATEGORY);

        assetsArray.add(asset);
        response.add("value", assetsArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockSystemTypeMapResponse() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();

        JsonObject asset = new JsonObject();
        asset.addProperty("_gua_systemtype_value", TEST_SYSTEM_TYPE_ID);
        asset.addProperty("msdyn_customerassetid", TEST_CUSTOMER_ASSET_ID);

        assetsArray.add(asset);
        response.add("value", assetsArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockEmptySystemTypeMapResponse() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();
        response.add("value", assetsArray);
        return Constants.GSON.toJson(response);
    }

    private String createMockSystemTypeMapResponseWithNullSystemType() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();

        JsonObject asset = new JsonObject();
        asset.add("_gua_systemtype_value", null); // Null system type
        asset.addProperty("msdyn_customerassetid", TEST_CUSTOMER_ASSET_ID);

        assetsArray.add(asset);
        response.add("value", assetsArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockSystemTypeMapResponseWithNullCustomerId() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();

        // Create an empty array since null customer asset ID means no matching records
        response.add("value", assetsArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockSystemTypeDetailsResponse() {
        JsonObject systemType = new JsonObject();
        systemType.addProperty("gua_systemtypeidmas", TEST_SYSTEM_TYPE_ID);
        systemType.addProperty("gua_name", "Test System Type");
        systemType.addProperty("gua_displaygroup", "Test Display Group");

        return Constants.GSON.toJson(systemType);
    }

    private String createMockMultipleCustomerAssetsResponse() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();

        // First asset
        JsonObject asset1 = new JsonObject();
        asset1.addProperty("msdyn_customerassetid", TEST_CUSTOMER_ASSET_ID);
        asset1.addProperty("msdyn_name", TEST_ASSET_NAME);
        asset1.addProperty("statuscode", TEST_STATUS_CODE);
        asset1.addProperty("stateCode", TEST_STATE_CODE);
        asset1.addProperty("createdon", TEST_CREATED_ON);
        asset1.addProperty("modifiedon", TEST_MODIFIED_ON);
        asset1.addProperty("_ownerid_value", TEST_OWNER_ID);
        asset1.addProperty("_msdyn_account_value", TEST_ACCOUNT_ID);
        asset1.addProperty("rsm_csnumber", TEST_CS_NUMBER);
        asset1.addProperty("gua_assetnumber", TEST_ASSET_NUMBER);
        asset1.addProperty("gua_ismonitoredasset", "true");
        asset1.addProperty("gua_inservicedate", "2023-01-01");
        asset1.addProperty("gua_lastservicedate", "2023-12-31");
        asset1.addProperty("_gua_serviceplan_value", TEST_SERVICE_PLAN);
        asset1.addProperty("gua_isactiveasset", "true");
        asset1.addProperty("gua_upgradetype", TEST_UPGRADE_TYPE);
        asset1.addProperty("gua_ooscategory", TEST_OOS_CATEGORY);

        // Second asset
        JsonObject asset2 = new JsonObject();
        asset2.addProperty("msdyn_customerassetid", "asset-456");
        asset2.addProperty("msdyn_name", "Test Asset 2");
        asset2.addProperty("statuscode", "2");
        asset2.addProperty("stateCode", "1");
        asset2.addProperty("createdon", "2023-02-01T10:00:00Z");
        asset2.addProperty("modifiedon", "2023-02-02T10:00:00Z");
        asset2.addProperty("_ownerid_value", "owner-456");
        asset2.addProperty("_msdyn_account_value", TEST_ACCOUNT_ID);
        asset2.addProperty("rsm_csnumber", "CS67890");
        asset2.addProperty("gua_assetnumber", "AN12345");
        asset2.addProperty("gua_ismonitoredasset", "false");
        asset2.addProperty("gua_inservicedate", "2023-02-01");
        asset2.addProperty("gua_lastservicedate", "2023-11-30");
        asset2.addProperty("_gua_serviceplan_value", "service-plan-456");
        asset2.addProperty("gua_isactiveasset", "false");
        asset2.addProperty("gua_upgradetype", "Premium");
        asset2.addProperty("gua_ooscategory", "Repair");

        assetsArray.add(asset1);
        assetsArray.add(asset2);
        response.add("value", assetsArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockMultipleSystemTypeMapResponse() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();

        JsonObject asset1 = new JsonObject();
        asset1.addProperty("_gua_systemtype_value", TEST_SYSTEM_TYPE_ID);
        asset1.addProperty("msdyn_customerassetid", TEST_CUSTOMER_ASSET_ID);

        JsonObject asset2 = new JsonObject();
        asset2.addProperty("_gua_systemtype_value", "system-type-789");
        asset2.addProperty("msdyn_customerassetid", "asset-456");

        assetsArray.add(asset1);
        assetsArray.add(asset2);
        response.add("value", assetsArray);

        return Constants.GSON.toJson(response);
    }

    private String createMockSystemTypeDetailsResponse2() {
        JsonObject systemType = new JsonObject();
        systemType.addProperty("gua_systemtypeidmas", "system-type-789");
        systemType.addProperty("gua_name", "Test System Type 2");
        systemType.addProperty("gua_displaygroup", "Test Display Group 2");

        return Constants.GSON.toJson(systemType);
    }

    private String createMockCustomerAssetsResponseWithFalseBooleans() {
        JsonObject response = new JsonObject();
        JsonArray assetsArray = new JsonArray();

        JsonObject asset = new JsonObject();
        asset.addProperty("msdyn_customerassetid", TEST_CUSTOMER_ASSET_ID);
        asset.addProperty("msdyn_name", TEST_ASSET_NAME);
        asset.addProperty("statuscode", TEST_STATUS_CODE);
        asset.addProperty("stateCode", TEST_STATE_CODE);
        asset.addProperty("createdon", TEST_CREATED_ON);
        asset.addProperty("modifiedon", TEST_MODIFIED_ON);
        asset.addProperty("_ownerid_value", TEST_OWNER_ID);
        asset.addProperty("_msdyn_account_value", TEST_ACCOUNT_ID);
        asset.addProperty("rsm_csnumber", TEST_CS_NUMBER);
        asset.addProperty("gua_assetnumber", TEST_ASSET_NUMBER);
        asset.addProperty("gua_ismonitoredasset", "false"); // Explicitly false
        asset.addProperty("gua_inservicedate", "2023-01-01");
        asset.addProperty("gua_lastservicedate", "2023-12-31");
        asset.addProperty("_gua_serviceplan_value", TEST_SERVICE_PLAN);
        asset.addProperty("gua_isactiveasset", "false"); // Explicitly false
        asset.addProperty("gua_upgradetype", TEST_UPGRADE_TYPE);
        asset.addProperty("gua_ooscategory", TEST_OOS_CATEGORY);

        assetsArray.add(asset);
        response.add("value", assetsArray);

        return Constants.GSON.toJson(response);
    }
}
