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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.CustomerAsset;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerAssetsDaoImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDaoImpl.class);
    private final CaseDaoImpl caseDao;
    private final RestClientProvider provider;

    @Inject
    public CustomerAssetsDaoImpl(RestClientProviderFactory providerFactory, CaseDaoImpl caseDao) {
        this(caseDao, providerFactory.create());
    }

    public CustomerAssetsDaoImpl(CaseDaoImpl caseDao, RestClientProvider provider) {
        this.caseDao = caseDao;
        this.provider = provider;
    }

    /**
     * Retrieves all customer assets for a specific account from Dynamics 365.
     * Enriches each asset with system type details (description, display group, etc.).
     *
     * @param accountId The account GUID to retrieve customer assets for
     * @return List of CustomerAsset entities with complete system type information
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    public List<CustomerAsset> getCustomerAssetsByAccountId(String accountId) throws MalformedURLException {
        JsonArray customerAssetsArray = fetchCustomerAssetsData(accountId);
        Map<String, Map<String, Object>> systemTypeMap = buildSystemTypeMap(accountId);

        return processCustomerAssets(customerAssetsArray, systemTypeMap);
    }

    /**
     * Builds a map of system type details for all customer assets of an account.
     * Fetches system type information for each asset and indexes by customer asset ID.
     *
     * @param accountId The account GUID to build system type map for
     * @return Map of customer asset ID to system type details (ID, description, value, display group)
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    private Map<String, Map<String, Object>> buildSystemTypeMap(String accountId) throws MalformedURLException {
        String endpoint = "msdyn_customerassets";
        Map<String, Object> queryParams2 = new HashMap<>();
        String filter = "_msdyn_account_value eq '" + accountId + "'";
        queryParams2.put("$filter", filter);
        queryParams2.put("$select", "_gua_systemtype_value");

        final String response2 = provider.getRestClient().get(endpoint, queryParams2);
        JsonArray customerAssetsArray2 = Constants.GSON.fromJson(response2, JsonObject.class)
                .getAsJsonArray(Constants.VALUE);

        Map<String, Map<String, Object>> res = new HashMap<>();
        for (JsonElement element : customerAssetsArray2) {
            JsonObject obj = element.getAsJsonObject();
            String systemTypeId = (obj.get("_gua_systemtype_value") != null && !obj.get("_gua_systemtype_value").isJsonNull()) ? obj.get("_gua_systemtype_value").getAsString() : null;
            String customerId = (obj.get("msdyn_customerassetid") != null && !obj.get("msdyn_customerassetid").isJsonNull()) ? obj.get("msdyn_customerassetid").getAsString() : null;

            if (systemTypeId != null) {
                Map<String, Object> systemTypeDetails = fetchSystemTypeDetails(systemTypeId);
                res.put(customerId, systemTypeDetails);
            }
        }
        return res;
    }

    /**
     * Processes raw customer asset JSON data into CustomerAsset entity objects.
     * Enriches each asset with system type information from the provided map.
     *
     * @param customerAssetsArray JsonArray of customer asset data from API
     * @param systemTypeMap Map of customer asset ID to system type details
     * @return List of CustomerAsset entities with system type information
     */
    private List<CustomerAsset> processCustomerAssets(JsonArray customerAssetsArray, Map<String, Map<String, Object>> systemTypeMap) {
        List<CustomerAsset> customerAssetsList = new ArrayList<>();

        for (JsonElement element : customerAssetsArray) {
            JsonObject obj = element.getAsJsonObject();
            CustomerAsset customerAsset = createCustomerAsset(obj, systemTypeMap);
            customerAssetsList.add(customerAsset);
        }
        return customerAssetsList;
    }

    /**
     * Fetches customer assets data from Dynamics 365 for a specific account.
     *
     * @param accountId The account GUID to filter by
     * @return JsonArray containing customer asset data
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    private JsonArray fetchCustomerAssetsData(String accountId) throws MalformedURLException {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("$filter", "_msdyn_account_value eq " + accountId);

        String endpoint = "msdyn_customerassets";
        final String response = provider.getRestClient().get(endpoint, queryParams);
        return Constants.GSON.fromJson(response, JsonObject.class)
                .getAsJsonArray(Constants.VALUE);
    }

    /**
     * Fetches system type details from Dynamics 365 for a specific system type ID.
     * Retrieves system type ID, description, value, and display group.
     *
     * @param systemTypeId The system type GUID to retrieve
     * @return Map containing "System Type Id", "System Type Description", "System Type Value", and "Display Group"
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    private Map<String, Object> fetchSystemTypeDetails(String systemTypeId) throws MalformedURLException {
        String filter = "gua_systemtypes(" + systemTypeId + ")";
        final String systemTypeResponse = provider.getRestClient().get(filter, new HashMap<>());
        JsonObject systemTypeObject = Constants.GSON.fromJson(systemTypeResponse, JsonObject.class);

        Map<String, Object> systemTypeMap = new HashMap<>();
        systemTypeMap.put("System Type Id", systemTypeObject.get("gua_systemtypeidmas"));
        systemTypeMap.put("System Type Description", systemTypeObject.get("gua_name"));
        systemTypeMap.put("System Type Value", systemTypeId);
        systemTypeMap.put("Display Group", systemTypeObject.get("gua_displaygroup"));
        return systemTypeMap;
    }

    /**
     * Creates a CustomerAsset entity from JSON data.
     * Maps all customer asset fields from Dynamics 365 API response to entity properties.
     * Enriches with system type information from the provided map.
     *
     * @param obj JsonObject containing customer asset data
     * @param systemTypeMap Map of customer asset ID to system type details
     * @return CustomerAsset entity populated with data and system type information
     */
    private CustomerAsset createCustomerAsset(JsonObject obj, Map<String, Map<String, Object>> systemTypeMap) {
        String customerAssetId = getAsString(obj, "msdyn_customerassetid");
        CustomerAssetsDaoImpl.SystemTypeInfo systemTypeInfo = extractSystemTypeInfo(customerAssetId, systemTypeMap);

        return CustomerAsset.builder()
                .customerAssetId(customerAssetId)
                .name(getAsString(obj, "msdyn_name"))
                .statusCode(getAsString(obj, "statuscode"))
                .stateCode(getAsString(obj, "stateCode"))
                .createdOn(getAsString(obj, "createdon"))
                .modifiedOn(getAsString(obj, "modifiedon"))
                .ownerId(getAsString(obj, "_ownerid_value"))
                .accountId(getAsString(obj, "_msdyn_account_value"))
                .cSNumber(getAsString(obj, "rsm_csnumber"))
                .assetNumber(getAsString(obj, "gua_assetnumber"))
                .isMonitoredAsset(Boolean.valueOf(getAsString(obj, "gua_ismonitoredasset")))
                .inServiceDate(getAsString(obj, "gua_inservicedate"))
                .outOfServiceDate(getAsString(obj, "gua_lastservicedate"))
                .servicePlan(getAsString(obj, "_gua_serviceplan_value"))
                .isActiveAsset(Boolean.valueOf(getAsString(obj, "gua_isactiveasset")))
                .upgradeType(getAsString(obj, "gua_upgradetype"))
                .oOSCategory(getAsString(obj, "gua_ooscategory"))
                .systemTypeId(systemTypeInfo.systemId)
                .systemTypeDescription(systemTypeInfo.description)
                .systemTypeValue(systemTypeInfo.systemValue)
                .displayGroup(systemTypeInfo.displayGroup)
                .build();
    }

    /**
     * Extracts system type information from the system type map for a specific customer asset.
     * Returns empty strings for all fields if system type information is not available.
     *
     * @param customerAssetId The customer asset ID to look up
     * @param systemTypeMap Map of customer asset ID to system type details
     * @return SystemTypeInfo containing system ID, description, value, and display group
     */
    private CustomerAssetsDaoImpl.SystemTypeInfo extractSystemTypeInfo(String customerAssetId, Map<String, Map<String, Object>> systemTypeMap) {
        String systemId = "";
        String description = "";
        String systemValue = "";
        String displayGroup = "";

        if (systemTypeMap.get(customerAssetId) != null) {
            systemId = systemTypeMap.get(customerAssetId).get("System Type Id").toString();
            description = systemTypeMap.get(customerAssetId).get("System Type Description").toString();
            systemValue = systemTypeMap.get(customerAssetId).get("System Type Value").toString();
            displayGroup = systemTypeMap.get(customerAssetId).get("Display Group").toString();
        }

        return new CustomerAssetsDaoImpl.SystemTypeInfo(systemId, description, systemValue, displayGroup);
    }

    private static class SystemTypeInfo {
        final String systemId;
        final String description;
        final String systemValue;
        final String displayGroup;

        SystemTypeInfo(String systemId, String description, String systemValue, String displayGroup) {
            this.systemId = systemId;
            this.description = description;
            this.systemValue = systemValue;
            this.displayGroup = displayGroup;
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