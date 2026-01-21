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

import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.CustomerAsset;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsAccount;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Account;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountDaoImplTest {

    private static final String TEST_ACCOUNT_ID = "account-123";
    private static final String TEST_ACCOUNT_NAME = "Test Account";
    private static final String TEST_PHONE = "555-1234";
    private static final String TEST_FAX = "555-5678";
    private static final String TEST_WEBSITE = "https://test.com";
    private static final String TEST_STREET1 = "123 Test Street";
    private static final String TEST_CITY = "Test City";
    private static final String TEST_STATE = "Test State";
    private static final String TEST_POSTAL_CODE = "12345";
    private static final String TEST_COUNTRY = "Test Country";
    private static final String TEST_PRIMARY_CONTACT_ID = "contact-456";
    private static final String TEST_PARENT_CUSTOMER_ID = "parent-789";

    @Mock
    private RestClientProviderFactory mockProviderFactory;

    @Mock
    private RestClientProvider mockProvider;

    @Mock
    private RestClient mockRestClient;

    @Mock
    private CustomerAssetsDaoImpl mockCustomerAssetsDao;

    private AccountDaoImpl accountDaoImpl;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockProviderFactory.create()).thenReturn(mockProvider);
        when(mockProvider.getRestClient()).thenReturn(mockRestClient);
        accountDaoImpl = new AccountDaoImpl(mockProvider, mockCustomerAssetsDao);
    }

    @Test
    public void testConstructor_WithProviderFactory_ShouldCreateInstance() {
        // Act
        AccountDaoImpl result = new AccountDaoImpl(mockProviderFactory, mockCustomerAssetsDao);

        // Assert
        assertNotNull(result);
        verify(mockProviderFactory).create();
    }

    @Test
    public void testConstructor_WithProvider_ShouldCreateInstance() {
        // Act
        AccountDaoImpl result = new AccountDaoImpl(mockProvider, mockCustomerAssetsDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testGetAccountById_WithValidId_ShouldReturnAccount() throws Exception {
        // Arrange
        String mockResponse = createMockAccountResponse();
        when(mockRestClient.get(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), any()))
                .thenReturn(mockResponse);

        // Act
        Account result = accountDaoImpl.getAccountById(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.getId());
        assertEquals(TEST_ACCOUNT_NAME, result.getName());
        assertEquals(TEST_PHONE, result.getPhone());
        verify(mockRestClient).get(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), any());
    }

    @Test
    public void testGetAccountById_WithNullId_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountDaoImpl.getAccountById(null);
        });

        assertTrue(exception.getMessage().contains("Unable to fetch account"));
        assertTrue(exception.getMessage().contains(Constants.ID_IS_NULL_OR_EMPTY));
    }

    @Test
    public void testGetAccountById_WithBlankId_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountDaoImpl.getAccountById("   ");
        });

        assertTrue(exception.getMessage().contains("Unable to fetch account"));
        assertTrue(exception.getMessage().contains(Constants.ID_IS_NULL_OR_EMPTY));
    }

    @Test
    public void testGetAllAccounts_ShouldCallGetAccountByQuery() throws Exception {
        // Arrange
        String mockResponse = createMockAccountsResponse();
        when(mockRestClient.get(eq(Constants.ACCOUNTS_API), any()))
                .thenReturn(mockResponse);

        // Act
        List<Account> result = accountDaoImpl.getAllAccounts(10, 1, "name", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mockRestClient).get(eq(Constants.ACCOUNTS_API), any());
    }

    @Test
    public void testGetAccountByQuery_WithValidQuery_ShouldReturnAccounts() throws Exception {
        // Arrange
        String mockResponse = createMockAccountsResponse();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("$filter", "name eq '" + TEST_ACCOUNT_NAME + "'");

        when(mockRestClient.get(eq(Constants.ACCOUNTS_API), eq(queryParams)))
                .thenReturn(mockResponse);

        // Act
        List<Account> result = accountDaoImpl.getAccountByQuery(queryParams);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        Account account = result.getFirst();
        assertEquals(TEST_ACCOUNT_ID, account.getId());
        assertEquals(TEST_ACCOUNT_NAME, account.getName());
        verify(mockRestClient).get(eq(Constants.ACCOUNTS_API), eq(queryParams));
    }

    @Test
    public void testGetAccountByQuery_WithEmptyResult_ShouldReturnEmptyList() throws Exception {
        // Arrange
        String mockResponse = createMockEmptyAccountsResponse();
        Map<String, Object> queryParams = new HashMap<>();

        when(mockRestClient.get(eq(Constants.ACCOUNTS_API), eq(queryParams)))
                .thenReturn(mockResponse);

        // Act
        List<Account> result = accountDaoImpl.getAccountByQuery(queryParams);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockRestClient).get(eq(Constants.ACCOUNTS_API), eq(queryParams));
    }

    @Test
    public void testCreateAccount_WithValidEntityDefinition_ShouldReturnAccountId() throws Exception {
        // Arrange
        Map<String, Object> entityDefinition = createMockEntityDefinition();
        String expectedAccountId = "new-account-123";

        when(mockRestClient.post(eq(Constants.ACCOUNTS_API), any(JSONObject.class)))
                .thenReturn(expectedAccountId);

        // Act
        String result = accountDaoImpl.createAccount(entityDefinition);

        // Assert
        assertEquals(expectedAccountId, result);
        verify(mockRestClient).post(eq(Constants.ACCOUNTS_API), any(JSONObject.class));
    }

    @Test
    public void testCreateAccount_WithMinimalEntityDefinition_ShouldCreatePayload() throws Exception {
        // Arrange
        Map<String, Object> entityDefinition = new HashMap<>();
        entityDefinition.put("Customer Name", TEST_ACCOUNT_NAME);
        String expectedAccountId = "new-account-123";

        when(mockRestClient.post(eq(Constants.ACCOUNTS_API), any(JSONObject.class)))
                .thenReturn(expectedAccountId);

        // Act
        String result = accountDaoImpl.createAccount(entityDefinition);

        // Assert
        assertEquals(expectedAccountId, result);
        verify(mockRestClient).post(eq(Constants.ACCOUNTS_API), any(JSONObject.class));
    }

    @Test
    public void testCreateAccount_WithNullPrimaryContactId_ShouldSkipContactBinding() throws Exception {
        // Arrange
        Map<String, Object> entityDefinition = createMockEntityDefinition();
        entityDefinition.put("Primary Contact Id", null);
        String expectedAccountId = "new-account-123";

        when(mockRestClient.post(eq(Constants.ACCOUNTS_API), any(JSONObject.class)))
                .thenReturn(expectedAccountId);

        // Act
        String result = accountDaoImpl.createAccount(entityDefinition);

        // Assert
        assertEquals(expectedAccountId, result);
        verify(mockRestClient).post(eq(Constants.ACCOUNTS_API), any(JSONObject.class));
    }

    @Test
    public void testCreateAccount_WithBlankPrimaryContactId_ShouldSkipContactBinding() throws Exception {
        // Arrange
        Map<String, Object> entityDefinition = createMockEntityDefinition();
        entityDefinition.put("Primary Contact Id", "   ");
        String expectedAccountId = "new-account-123";

        when(mockRestClient.post(eq(Constants.ACCOUNTS_API), any(JSONObject.class)))
                .thenReturn(expectedAccountId);

        // Act
        String result = accountDaoImpl.createAccount(entityDefinition);

        // Assert
        assertEquals(expectedAccountId, result);
        verify(mockRestClient).post(eq(Constants.ACCOUNTS_API), any(JSONObject.class));
    }

    @Test
    public void testCreateAccount_WithNullParentCustomerId_ShouldSkipParentBinding() throws Exception {
        // Arrange
        Map<String, Object> entityDefinition = createMockEntityDefinition();
        entityDefinition.put("Parent Customer Id", null);
        String expectedAccountId = "new-account-123";

        when(mockRestClient.post(eq(Constants.ACCOUNTS_API), any(JSONObject.class)))
                .thenReturn(expectedAccountId);

        // Act
        String result = accountDaoImpl.createAccount(entityDefinition);

        // Assert
        assertEquals(expectedAccountId, result);
        verify(mockRestClient).post(eq(Constants.ACCOUNTS_API), any(JSONObject.class));
    }

    @Test
    public void testCreateAccount_WithBlankParentCustomerId_ShouldSkipParentBinding() throws Exception {
        // Arrange
        Map<String, Object> entityDefinition = createMockEntityDefinition();
        entityDefinition.put("Parent Customer Id", "   ");
        String expectedAccountId = "new-account-123";

        when(mockRestClient.post(eq(Constants.ACCOUNTS_API), any(JSONObject.class)))
                .thenReturn(expectedAccountId);

        // Act
        String result = accountDaoImpl.createAccount(entityDefinition);

        // Assert
        assertEquals(expectedAccountId, result);
        verify(mockRestClient).post(eq(Constants.ACCOUNTS_API), any(JSONObject.class));
    }

    // Helper methods for creating mock objects and responses
    private String createMockAccountResponse() {
        return "{"
                + "\"accountid\":\"" + TEST_ACCOUNT_ID + "\","
                + "\"name\":\"" + TEST_ACCOUNT_NAME + "\","
                + "\"telephone1\":\"" + TEST_PHONE + "\","
                + "\"fax\":\"" + TEST_FAX + "\","
                + "\"websiteurl\":\"" + TEST_WEBSITE + "\","
                + "\"address1_line1\":\"" + TEST_STREET1 + "\","
                + "\"address1_city\":\"" + TEST_CITY + "\","
                + "\"address1_stateorprovince\":\"" + TEST_STATE + "\","
                + "\"address1_postalcode\":\"" + TEST_POSTAL_CODE + "\","
                + "\"address1_country\":\"" + TEST_COUNTRY + "\","
                + "\"_primarycontactid_value\":\"" + TEST_PRIMARY_CONTACT_ID + "\","
                + "\"_parentaccountid_value\":\"" + TEST_PARENT_CUSTOMER_ID + "\""
                + "}";
    }

    private String createMockAccountsResponse() {
        return "{"
                + "\"value\": ["
                + createMockAccountResponse()
                + "]"
                + "}";
    }

    private String createMockEmptyAccountsResponse() {
        return "{"
                + "\"value\": []"
                + "}";
    }

    @Test
    public void testUpdateAccount_WithValidAccount_ShouldReturnUpdatedAccount() throws Exception {
        // Arrange
        Account mockAccount = createMockAccount();
        String getAccountResponse = createMockAccountResponse();
        String updateResponse = createMockUpdatedAccountResponse();

        when(mockRestClient.get(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), any()))
                .thenReturn(getAccountResponse);
        when(mockRestClient.patch(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), anyString()))
                .thenReturn(updateResponse);

        // Act
        Account result = accountDaoImpl.updateAccount(mockAccount);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Account Name", result.getName());
        verify(mockRestClient).get(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), any());
        verify(mockRestClient).patch(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), anyString());
    }

    @Test
    public void testUpdateAccount_WithNoChanges_ShouldReturnUpdatedAccount() throws Exception {
        // Arrange
        Account mockAccount = createMockAccount();
        String getAccountResponse = createMockAccountResponse();
        String updateResponse = createMockAccountResponse(); // Same as original

        when(mockRestClient.get(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), any()))
                .thenReturn(getAccountResponse);
        when(mockRestClient.patch(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), anyString()))
                .thenReturn(updateResponse);

        // Act
        Account result = accountDaoImpl.updateAccount(mockAccount);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_NAME, result.getName());
        verify(mockRestClient).get(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), any());
        verify(mockRestClient).patch(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), anyString());
    }

    @Test
    public void testDeleteAccountById_WithValidId_ShouldCallDelete() throws Exception {
        // Act
        accountDaoImpl.deleteAccountById(TEST_ACCOUNT_ID);

        // Assert
        verify(mockRestClient).delete(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")");
    }

    @Test
    public void testGetLookupAccountByAccountId_WithValidAccountId_ShouldReturnLookupAccount() throws Exception {
        // Arrange
        String mockAccountResponse = createMockLookupAccountResponse();
        List<CustomerAsset> mockCustomerAssets = createMockCustomerAssets();

        when(mockCustomerAssetsDao.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID))
                .thenReturn(mockCustomerAssets);
        when(mockRestClient.get(eq("accounts(" + TEST_ACCOUNT_ID + ")"), any()))
                .thenReturn(mockAccountResponse);

        // Act
        com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.Account result =
                accountDaoImpl.getLookupAccountByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.accountId);
        assertEquals(TEST_ACCOUNT_NAME, result.accountName);
        assertEquals(TEST_STREET1, result.address);
        assertEquals(TEST_CITY, result.city);
        assertEquals(1, result.listOfCustomerAssets.size());
        verify(mockCustomerAssetsDao).getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);
        verify(mockRestClient).get(eq("accounts(" + TEST_ACCOUNT_ID + ")"), any());
    }

    @Test
    public void testGetLookupAccountByAccountId_WithInactiveAssets_ShouldFilterActiveAssets() throws Exception {
        // Arrange
        String mockAccountResponse = createMockLookupAccountResponse();
        List<CustomerAsset> mockCustomerAssets = createMockCustomerAssetsWithInactive();

        when(mockCustomerAssetsDao.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID))
                .thenReturn(mockCustomerAssets);
        when(mockRestClient.get(eq("accounts(" + TEST_ACCOUNT_ID + ")"), any()))
                .thenReturn(mockAccountResponse);

        // Act
        com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.Account result =
                accountDaoImpl.getLookupAccountByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.listOfCustomerAssets.size()); // Only active assets
        verify(mockCustomerAssetsDao).getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);
        verify(mockRestClient).get(eq("accounts(" + TEST_ACCOUNT_ID + ")"), any());
    }

    @Test
    public void testGetLookupAccountByAccountId_WithException_ShouldThrowRuntimeException() throws Exception {
        // Arrange
        when(mockCustomerAssetsDao.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID))
                .thenThrow(new RuntimeException("Customer assets error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountDaoImpl.getLookupAccountByAccountId(TEST_ACCOUNT_ID);
        });

        assertTrue(exception.getMessage().contains("Unable to retrieve lookup account details"));
        verify(mockCustomerAssetsDao).getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);
    }

    @Test
    public void testGetLookupAccountByAccountId_WithNullFields_ShouldHandleNullValues() throws Exception {
        // Arrange
        String mockAccountResponse = createMockLookupAccountResponseWithNulls();
        List<CustomerAsset> mockCustomerAssets = createMockCustomerAssetsWithNulls();

        when(mockCustomerAssetsDao.getCustomerAssetsByAccountId(TEST_ACCOUNT_ID))
                .thenReturn(mockCustomerAssets);
        when(mockRestClient.get(eq("accounts(" + TEST_ACCOUNT_ID + ")"), any()))
                .thenReturn(mockAccountResponse);

        // Act
        com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.Account result =
                accountDaoImpl.getLookupAccountByAccountId(TEST_ACCOUNT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.accountId);
        assertNull(result.accountName);
        assertEquals(1, result.listOfCustomerAssets.size());
        Map<String, Object> asset = result.listOfCustomerAssets.getFirst();
        assertEquals("N/A", asset.get("Customer Asset Name"));
        assertEquals("N/A", asset.get("Display Group"));
        verify(mockCustomerAssetsDao).getCustomerAssetsByAccountId(TEST_ACCOUNT_ID);
        verify(mockRestClient).get(eq("accounts(" + TEST_ACCOUNT_ID + ")"), any());
    }

    @Test
    public void testGetAccountByQuery_WithJsonSyntaxException_ShouldPropagateException() {
        // Arrange
        String invalidJsonResponse = "invalid json";
        Map<String, Object> queryParams = new HashMap<>();

        when(mockRestClient.get(eq(Constants.ACCOUNTS_API), eq(queryParams)))
                .thenReturn(invalidJsonResponse);

        // Act & Assert
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> {
            accountDaoImpl.getAccountByQuery(queryParams);
        });

        verify(mockRestClient).get(eq(Constants.ACCOUNTS_API), eq(queryParams));
    }

    @Test
    public void testGetAccountById_WithRuntimeException_ShouldPropagateException() {
        // Arrange
        when(mockRestClient.get(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), any()))
                .thenThrow(new RuntimeException("API error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountDaoImpl.getAccountById(TEST_ACCOUNT_ID);
        });

        assertEquals("API error", exception.getMessage());
        verify(mockRestClient).get(eq(Constants.ACCOUNTS_API + "(" + TEST_ACCOUNT_ID + ")"), any());
    }

    // Helper methods for creating mock objects and responses
    private Account createMockAccount() {
        return new AccountImpl(createMockDynamicsAccount());
    }

    private DynamicsAccount createMockDynamicsAccount() {
        DynamicsAccount account = new DynamicsAccount();
        account.accountId = TEST_ACCOUNT_ID;
        account.name = TEST_ACCOUNT_NAME;
        account.phone = TEST_PHONE;
        account.fax = TEST_FAX;
        account.website = TEST_WEBSITE;
        account.street1 = TEST_STREET1;
        account.city = TEST_CITY;
        account.state = TEST_STATE;
        account.postalCode = TEST_POSTAL_CODE;
        account.country = TEST_COUNTRY;
        account.primaryContactId = TEST_PRIMARY_CONTACT_ID;
        account.parentCustomerId = TEST_PARENT_CUSTOMER_ID;
        return account;
    }

    private String createMockUpdatedAccountResponse() {
        return "{"
                + "\"accountid\":\"" + TEST_ACCOUNT_ID + "\","
                + "\"name\":\"Updated Account Name\","
                + "\"telephone1\":\"" + TEST_PHONE + "\","
                + "\"fax\":\"" + TEST_FAX + "\","
                + "\"websiteurl\":\"" + TEST_WEBSITE + "\","
                + "\"address1_line1\":\"" + TEST_STREET1 + "\","
                + "\"address1_city\":\"" + TEST_CITY + "\","
                + "\"address1_stateorprovince\":\"" + TEST_STATE + "\","
                + "\"address1_postalcode\":\"" + TEST_POSTAL_CODE + "\","
                + "\"address1_country\":\"" + TEST_COUNTRY + "\","
                + "\"_primarycontactid_value\":\"" + TEST_PRIMARY_CONTACT_ID + "\","
                + "\"_parentaccountid_value\":\"" + TEST_PARENT_CUSTOMER_ID + "\""
                + "}";
    }

    private String createMockLookupAccountResponse() {
        return "{"
                + "\"accountid\":\"" + TEST_ACCOUNT_ID + "\","
                + "\"name\":\"" + TEST_ACCOUNT_NAME + "\","
                + "\"address1_line1\":\"" + TEST_STREET1 + "\","
                + "\"address1_city\":\"" + TEST_CITY + "\","
                + "\"accountnumber\":\"ACC-123\","
                + "\"telephone1\":\"" + TEST_PHONE + "\","
                + "\"gua_adcbranchid\":\"BRANCH-456\""
                + "}";
    }

    private String createMockLookupAccountResponseWithNulls() {
        return "{"
                + "\"accountid\":\"" + TEST_ACCOUNT_ID + "\","
                + "\"name\":null,"
                + "\"address1_line1\":null,"
                + "\"address1_city\":null,"
                + "\"accountnumber\":null,"
                + "\"telephone1\":null,"
                + "\"gua_adcbranchid\":null"
                + "}";
    }

    private List<CustomerAsset> createMockCustomerAssets() {
        List<CustomerAsset> assets = new ArrayList<>();
        CustomerAsset asset = CustomerAsset.builder()
                .customerAssetId("asset-123")
                .name("Test Asset")
                .systemTypeValue("system-type-1")
                .isActiveAsset(true)
                .systemTypeDescription("Test System Type")
                .displayGroup("Group A")
                .build();
        assets.add(asset);
        return assets;
    }

    private List<CustomerAsset> createMockCustomerAssetsWithInactive() {
        List<CustomerAsset> assets = new ArrayList<>();

        CustomerAsset activeAsset = CustomerAsset.builder()
                .customerAssetId("asset-123")
                .name("Active Asset")
                .systemTypeValue("system-type-1")
                .isActiveAsset(true)
                .systemTypeDescription("Test System Type")
                .displayGroup("Group A")
                .build();
        assets.add(activeAsset);

        CustomerAsset inactiveAsset = CustomerAsset.builder()
                .customerAssetId("asset-456")
                .name("Inactive Asset")
                .systemTypeValue("system-type-2")
                .isActiveAsset(false)
                .systemTypeDescription("Test System Type 2")
                .displayGroup("Group B")
                .build();
        assets.add(inactiveAsset);

        return assets;
    }

    private List<CustomerAsset> createMockCustomerAssetsWithNulls() {
        List<CustomerAsset> assets = new ArrayList<>();
        CustomerAsset asset = CustomerAsset.builder()
                .customerAssetId("asset-123")
                .name(null) // Will be handled as "N/A"
                .systemTypeValue("system-type-1")
                .isActiveAsset(true)
                .systemTypeDescription("Test System Type")
                .displayGroup(null) // Will be handled as "N/A"
                .build();
        assets.add(asset);
        return assets;
    }

    private Map<String, Object> createMockEntityDefinition() {
        Map<String, Object> entityDefinition = new HashMap<>();
        entityDefinition.put("Customer Name", TEST_ACCOUNT_NAME);
        entityDefinition.put("Phone", TEST_PHONE);
        entityDefinition.put("Fax", TEST_FAX);
        entityDefinition.put("Website", TEST_WEBSITE);
        entityDefinition.put("Primary Contact Id", TEST_PRIMARY_CONTACT_ID);
        entityDefinition.put("Parent Customer Id", TEST_PARENT_CUSTOMER_ID);
        entityDefinition.put("Street1", TEST_STREET1);
        entityDefinition.put("Street2", "Suite 100");
        entityDefinition.put("Street3", "Building A");
        entityDefinition.put("City", TEST_CITY);
        entityDefinition.put("State", TEST_STATE);
        entityDefinition.put("Postal Code", TEST_POSTAL_CODE);
        entityDefinition.put("Country", TEST_COUNTRY);
        return entityDefinition;
    }
}
