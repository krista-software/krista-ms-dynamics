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
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.CustomerAsset;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsAccount;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Account;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.AccountDao;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public final class AccountDaoImpl implements AccountDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDaoImpl.class);

    public static final String PARENT_CUSTOMER_ID_ODATA_BIND = "parentaccountid@odata.bind";
    public static final String PRIMARY_CONTACT_ID_ODATA_BIND = "primarycontactid@odata.bind";
    private final RestClientProvider provider;
    private final CustomerAssetsDaoImpl customerAssetsDao;

    @Inject
    public AccountDaoImpl(RestClientProviderFactory providerFactory, CustomerAssetsDaoImpl customerAssetsDao) {
        this(providerFactory.create(), customerAssetsDao);
    }

    public AccountDaoImpl(RestClientProvider provider, CustomerAssetsDaoImpl customerAssetsDao) {
        this.provider = provider;
        this.customerAssetsDao = customerAssetsDao;
    }

    /**
     * Constructs the JSON payload for creating a new account in Dynamics 365.
     * Maps entity definition fields to Dynamics 365 API field names and handles OData bindings for relationships.
     *
     * @param entityDefinition Map containing account field values with keys matching catalog entity field names
     * @return JSONObject formatted for Dynamics 365 account creation API
     * @throws JSONException if JSON construction fails
     */
    @NotNull
    private static JSONObject getCreateAccountPayload(Map<String, Object> entityDefinition) throws JSONException {
        JSONObject target = new JSONObject();
        target.put("name", entityDefinition.get("Customer Name"));
        target.put("telephone1", entityDefinition.get("Phone"));
        target.put("fax", entityDefinition.get("Fax"));
        target.put("websiteurl", entityDefinition.get("Website"));
        String primaryContactIdValue = (String) entityDefinition.get("Primary Contact Id");
        if (primaryContactIdValue != null && !primaryContactIdValue.isBlank()) {
            target.put(PRIMARY_CONTACT_ID_ODATA_BIND, "/contacts(" + primaryContactIdValue + ")");
        }
        String getParentCustomerIdValue = (String) entityDefinition.get("Parent Customer Id");
        if (getParentCustomerIdValue != null && !getParentCustomerIdValue.isBlank()) {
            target.put(PARENT_CUSTOMER_ID_ODATA_BIND, "/accounts(" + getParentCustomerIdValue + ")");
        }
        target.put("address1_line1", entityDefinition.get("Street1"));
        target.put("address1_line2", entityDefinition.get("Street2"));
        target.put("address1_line3", entityDefinition.get("Street3"));
        target.put("address1_city", entityDefinition.get("City"));
        target.put("address1_stateorprovince", entityDefinition.get("State"));
        target.put("address1_postalcode", entityDefinition.get("Postal Code"));
        target.put("address1_country", entityDefinition.get("Country"));

        return target;
    }

    /**
     * Retrieves a single account by its unique identifier from Dynamics 365.
     *
     * @param id The account GUID (accountid) to retrieve
     * @return Account object containing the account details
     * @throws IOException if network communication fails
     * @throws ExecutionException if async operation fails
     * @throws InterruptedException if operation is interrupted
     * @throws JSONException if JSON parsing fails
     * @throws IllegalArgumentException if id is null or blank
     */
    @Override
    public Account getAccountById(String id) throws IOException, ExecutionException, InterruptedException,
            JSONException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Unable to fetch account. " + Constants.ID_IS_NULL_OR_EMPTY);
        }
        return new AccountImpl(Constants.GSON.fromJson(provider
                        .getRestClient()
                        .get(Constants.ACCOUNTS_API + "(" + id + ")", new HashMap<>()),
                DynamicsAccount.class));
    }

    /**
     * Retrieves all accounts without filtering. Delegates to getAccountByQuery with empty parameters.
     *
     * @param size Pagination size (currently unused, returns all results)
     * @param page Page index (currently unused)
     * @param orderByField Field name for sorting (currently unused)
     * @param orderByOperand Sort direction (currently unused)
     * @return List of all Account objects
     * @throws IOException if network communication fails
     */
    @Override
    public List<Account> getAllAccounts(long size, long page, String orderByField, String orderByOperand)
            throws IOException {
        return getAccountByQuery(new HashMap<>());
    }

    /**
     * Retrieves accounts based on OData query parameters.
     * Executes GET request to Dynamics 365 accounts API and maps results to Account objects.
     *
     * @param queryParams Map of OData query parameters (e.g., $filter, $select, $orderby)
     * @return List of Account objects matching the query, or empty list if no results
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    @Override
    public List<Account> getAccountByQuery(Map<String, Object> queryParams) throws MalformedURLException {

        String result = provider.getRestClient().get(Constants.ACCOUNTS_API, queryParams);
        JsonArray jsonArrayResult = Constants.GSON.fromJson(result, JsonObject.class).getAsJsonArray(Constants.VALUE);

        Type listType = new TypeToken<List<DynamicsAccount>>() {
        }.getType();
        List<DynamicsAccount> accounts = Constants.GSON.fromJson(jsonArrayResult.toString(), listType);

        if (!accounts.isEmpty()) {
            return accounts.stream().map(AccountImpl::new).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Creates a new account in Dynamics 365.
     * Constructs the payload from entity definition and posts to the accounts API.
     *
     * @param entityDefinition Map containing account field values
     * @return JSON response string from Dynamics 365 containing the created account details
     * @throws IOException if network communication fails
     * @throws ExecutionException if async operation fails
     * @throws InterruptedException if operation is interrupted
     * @throws JSONException if JSON construction or parsing fails
     */
    @Override
    public String createAccount(Map<String, Object> entityDefinition) throws IOException, ExecutionException,
            InterruptedException, JSONException {
        return provider.getRestClient().post(Constants.ACCOUNTS_API, getCreateAccountPayload(entityDefinition));
    }

    /**
     * Updates an existing account in Dynamics 365.
     * Compares old and new account states, sends only changed fields via PATCH request.
     *
     * @param account Account object containing updated field values
     * @return Updated Account object with latest values from Dynamics 365
     * @throws IllegalArgumentException if account data is invalid
     * @throws JSONException if JSON processing fails
     * @throws IOException if network communication fails
     * @throws ExecutionException if async operation fails
     * @throws InterruptedException if operation is interrupted
     * @throws IllegalAccessException if reflection access to fields fails
     */
    @Override
    public Account updateAccount(Account account) throws IllegalArgumentException, JSONException, IOException,
            ExecutionException, InterruptedException, IllegalAccessException {
        DynamicsAccount updatedAccount = DynamicsAccount.from(account);
        DynamicsAccount oldAccount = DynamicsAccount.from(getAccountById(account.getId()));
        Map<String, String> differenceInAccounts = getDifference(oldAccount, updatedAccount);
        return new AccountImpl(Constants.GSON.fromJson(provider
                        .getRestClient()
                        .patch(Constants.ACCOUNTS_API + "(" + account.getId() + ")",
                                Constants.GSON.toJson(differenceInAccounts)),
                DynamicsAccount.class));
    }

    /**
     * Compares two DynamicsAccount objects and returns a map of changed fields.
     * Uses reflection to iterate through all fields and identify differences.
     *
     * @param oldAccount Original account state from Dynamics 365
     * @param updatedAccount New account state with changes
     * @return Map of field names to new values for fields that changed (excluding customerId)
     * @throws IllegalAccessException if reflection access to fields fails
     */
    private Map<String, String> getDifference(DynamicsAccount oldAccount,
                                              DynamicsAccount updatedAccount) throws IllegalAccessException {
        Map<String, String> differences = new HashMap<>();
        Field[] fields = DynamicsAccount.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            Object oldValue = field.get(oldAccount);
            oldValue = (oldValue == null) ? "" : oldValue;

            Object updatedValue = field.get(updatedAccount);
            updatedValue = (updatedValue == null) ? "" : updatedValue;

            if ((!Objects.equals(oldValue, updatedValue)) &&
                    (!field.getName().equalsIgnoreCase("customerId"))) {
                String payloadName = getPayloadName(field);
                if (payloadName != null && !payloadName.isBlank()) {
                    differences.put(payloadName, getPayloadValue(payloadName, (String) updatedValue));
                }
            }
        }
        return differences;
    }

    /**
     * Maps Java field names to Dynamics 365 API payload field names.
     * Handles special cases for relationship fields that require OData binding syntax.
     *
     * @param field The Java Field object to map
     * @return Dynamics 365 API field name or OData binding name
     */
    private String getPayloadName(Field field) {
        if (field.getName().equalsIgnoreCase("parentCustomerId")) {
            return PARENT_CUSTOMER_ID_ODATA_BIND;
        } else if (field.getName().equalsIgnoreCase("primaryContactId")) {
            return PRIMARY_CONTACT_ID_ODATA_BIND;
        } else {
            return field.getAnnotation(SerializedName.class).value();
        }
    }

    /**
     * Formats field values for Dynamics 365 API payload.
     * Converts relationship IDs to OData binding format.
     *
     * @param field The field name being updated
     * @param updatedValue The new value for the field
     * @return Formatted value (OData binding for relationships, raw value otherwise)
     */
    private String getPayloadValue(String field, String updatedValue) {
        if (field.equalsIgnoreCase(PARENT_CUSTOMER_ID_ODATA_BIND)) {
            return "/accounts(" + updatedValue + ")";
        } else if (field.equalsIgnoreCase(PRIMARY_CONTACT_ID_ODATA_BIND)) {
            return "/contacts(" + updatedValue + ")";
        } else {
            return updatedValue;
        }
    }

    /**
     * Deletes an account from Dynamics 365 by its unique identifier.
     *
     * @param id The account GUID (accountid) to delete
     * @throws InterruptedException if operation is interrupted
     * @throws ExecutionException if async operation fails
     * @throws JSONException if JSON processing fails
     * @throws IOException if network communication fails
     */
    @Override
    public void deleteAccountById(String id) throws InterruptedException, ExecutionException, JSONException,
            IOException {
        provider.getRestClient().delete(Constants.ACCOUNTS_API + "(" + id + ")");
    }

    /**
     * Retrieves account details with associated active customer assets.
     * Combines account information from Dynamics 365 with filtered customer asset data.
     * Only includes assets where isActiveAsset is true.
     *
     * @param accountId The account GUID to retrieve
     * @return Account catalog entity containing account details and list of active customer assets
     * @throws MalformedURLException if the API endpoint URL is invalid
     * @throws RuntimeException if account retrieval fails or account doesn't exist
     */
    @Override
    public com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.Account getLookupAccountByAccountId(String accountId) throws MalformedURLException {
        LOGGER.info("Getting lookup account by account ID: {}", accountId);
        try {
            Map<String, Object> queryParams = new HashMap<>();
            List<Map<String, Object>> finalResponseList = new ArrayList<>();
            String endpoint = "accounts(" + accountId + ")";

            LOGGER.debug("Fetching customer assets for account ID: {}", accountId);
            List<CustomerAsset> customerAssetList = customerAssetsDao.getCustomerAssetsByAccountId(accountId);
            LOGGER.debug("Retrieved {} customer assets for account ID: {}", customerAssetList.size(), accountId);

            int activeAssetsCount = 0;
            for (CustomerAsset customerAsset : customerAssetList) {
                if (customerAsset.isActiveAsset) {
                    activeAssetsCount++;
                    Map<String, Object> customerAssetDetails = new HashMap<>();
                    customerAssetDetails.put("Customer Asset Id", customerAsset.customerAssetId);
                    customerAssetDetails.put("Customer Asset Name", customerAsset.name != null ? customerAsset.name : "N/A");
                    customerAssetDetails.put("System Type Value", customerAsset.systemTypeValue);
                    customerAssetDetails.put("Is Active", customerAsset.isActiveAsset);
                    customerAssetDetails.put("Gua Name", customerAsset.systemTypeDescription);
                    customerAssetDetails.put("Display Group", customerAsset.displayGroup != null ? customerAsset.displayGroup : "N/A");
                    finalResponseList.add(customerAssetDetails);
                }
            }

            LOGGER.debug("Processed {} active assets out of {} total assets for account ID: {}",
                    activeAssetsCount, customerAssetList.size(), accountId);

            LOGGER.debug("Executing Dynamics API call with endpoint: {}", endpoint);
            final String response = provider.getRestClient().get(endpoint, queryParams);
            LOGGER.debug("Received account details response from Dynamics API");

            JsonObject customerAssetsArray = Constants.GSON.fromJson(response, JsonObject.class).getAsJsonObject();

            com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.Account account = new com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.Account(
                    getAsString(customerAssetsArray, "address1_line1"),
                    getAsString(customerAssetsArray, "name"),
                    getAsString(customerAssetsArray, "accountid"),
                    getAsString(customerAssetsArray, "address1_city"),
                    finalResponseList,
                    getAsString(customerAssetsArray, "accountnumber"),
                    getAsString(customerAssetsArray, "telephone1"),
                    getAsString(customerAssetsArray, "gua_adcbranchid")
            );

            LOGGER.info("Successfully retrieved lookup account for ID: {} with {} active assets",
                    accountId, finalResponseList.size());
            return account;
        } catch (Exception cause) {
            LOGGER.error("Error occurred while getting lookup account for account ID: {} - {}", accountId, cause.getMessage());
            throw new RuntimeException("Unable to retrieve lookup account details. Verify the account ID exists and you have read permissions. Error: " + cause.getMessage(), cause);
        }
    }

    /**
     * Safely extracts a string value from a JsonObject.
     * Returns null if the key doesn't exist or the value is JSON null.
     *
     * @param obj The JsonObject to extract from
     * @param key The key to look up
     * @return String value or null if not present or null
     */
    private String getAsString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : null;
    }

}