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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.stores;

import app.krista.extension.executor.QueryClause;
import app.krista.extension.executor.SearchCondition;
import app.krista.extension.executor.SearchQuery;
import app.krista.extension.util.EntityStore;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.CatalogTypes;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.catalog.entities.MsDynamicsContact;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.ContactDaoImpl;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.ContactImpl;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsContact;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Contact;
import org.codehaus.jettison.json.JSONException;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants.*;
import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.getSearchConditionStringOperator;
import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.getSearchConditionValue;

@Service
public class MsDynamicsContactStore implements EntityStore<MsDynamicsContact> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsDynamicsContactStore.class);
    public static final String LAST_NAME = "lastName";
    private static final Object QUERY_FIELDS = "firstname, lastname, contactid, _parentcustomerid_value, mobilephone, " +
            "fax, address1_line1, address1_line2, address1_line3, address1_city, address1_stateorprovince, " +
            "address1_postalcode, address1_country, emailaddress1, preferredcontactmethodcode";
    private final ContactDaoImpl contactDao;
    private final RestClientProvider provider;

    @Inject
    private MsDynamicsContactStore(RestClientProviderFactory providerFactory, ContactDaoImpl contactDao) {
        this(contactDao, providerFactory.create());
    }

    public MsDynamicsContactStore(ContactDaoImpl contactDao, RestClientProvider provider) {
        this.contactDao = contactDao;
        this.provider = provider;
    }

    /**
     * Creates a new contact in Dynamics 365.
     * Validates entity definition and delegates to ContactDaoImpl for creation.
     *
     * @param entityDefinition Map containing contact field values
     * @return MsDynamicsContact entity representing the created contact
     * @throws IllegalArgumentException if entity definition is null or empty
     * @throws RuntimeException if contact data format is invalid
     */
    @Override
    public MsDynamicsContact create(Map<String, Object> entityDefinition) {
        if (entityDefinition == null || entityDefinition.isEmpty()) {
            throw new IllegalArgumentException(Constants.FAILED_TO_CREATE + Constants.EMPTY_ENTITY);
        }
        try {
            Contact createdContact = new ContactImpl(
                    Constants.GSON.fromJson(contactDao.createContact(entityDefinition), DynamicsContact.class)
            );
            return CatalogTypes.fromDynamicsToKristaEntity(createdContact);
        } catch (JsonSyntaxException | MalformedURLException | JSONException cause) {
            throw new RuntimeException("Invalid contact data format. Verify all field values match the expected data types and structure.", cause);
        }
    }

    /**
     * Retrieves a contact from Dynamics 365 by its unique identifier.
     *
     * @param primaryKey The contact GUID (contactid)
     * @return MsDynamicsContact entity representing the contact
     * @throws IOException if API communication fails
     * @throws IllegalStateException if contact ID is invalid or contact doesn't exist
     */
    @Override
    public MsDynamicsContact get(String primaryKey) throws IOException {
        try {
            return CatalogTypes.fromDynamicsToKristaEntity(contactDao.getContactById(primaryKey));
        } catch (Exception cause) {
            throw new IllegalStateException("Invalid contact ID. Verify the contact ID is a valid GUID and the contact exists.", cause);
        }
    }

    /**
     * Updates an existing contact in Dynamics 365.
     * Compares old and new states, sends only changed fields.
     *
     * @param dynamicsContact MsDynamicsContact entity with updated field values
     * @return MsDynamicsContact entity representing the updated contact
     * @throws IllegalArgumentException if dynamicsContact is null
     * @throws IllegalStateException if contact update fails or contact ID doesn't exist
     */
    @Override
    public MsDynamicsContact update(MsDynamicsContact dynamicsContact) {
        if (dynamicsContact == null) {
            throw new IllegalArgumentException(Constants.FAILED_TO_UPDATE + Constants.EMPTY_ENTITY);
        }
        try {
            return CatalogTypes.fromDynamicsToKristaEntity(contactDao.updateContact(
                    new ContactImpl(DynamicsContact.from(dynamicsContact))
            ));
        } catch (Exception cause) {
            throw new IllegalStateException("Invalid contact update data. Verify the contact ID exists and all field values are valid.", cause);
        }
    }

    /**
     * Deletes a contact from Dynamics 365 by its unique identifier.
     *
     * @param primaryKey The contact GUID (contactid) to delete
     * @throws IllegalArgumentException if primary key is null or blank
     * @throws IllegalStateException if contact ID format is invalid
     */
    @Override
    public void delete(String primaryKey) {
        if (primaryKey == null || primaryKey.isBlank()) {
            throw new IllegalArgumentException(Constants.FAILED_TO_DELETE + Constants.PRIMARY_KEY_IS_EMPTY);
        }
        try {
            contactDao.deleteAccountById(primaryKey);
        } catch (Exception cause) {
            throw new IllegalStateException("Invalid contact ID format. Verify the contact ID is a valid GUID.", cause);
        }
    }

    @Override
    public boolean contains(String s) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<MsDynamicsContact> search(List<SearchCondition> list, long l, int i) {
        return Collections.emptyList();
    }

    /**
     * Searches for contacts in Dynamics 365 based on search conditions and query clauses.
     * Supports filtering by last name, contact ID, and account ID.
     * Supports ordering and pagination.
     *
     * @param searchQuery SearchQuery containing search conditions and query clauses
     * @param pageIndex Page number (1-based) for pagination
     * @param pageSize Number of results per page
     * @return List of MsDynamicsContact entities matching the search criteria
     * @throws IllegalArgumentException if search conditions are null or empty
     * @throws RuntimeException if API communication fails
     */
    @Override
    public List<MsDynamicsContact> search(SearchQuery searchQuery, long pageIndex, int pageSize) {
        final List<SearchCondition> conditions = searchQuery.getSearchConditions();
        final List<QueryClause> queryClauses = searchQuery.getQueryClauses();
        if (conditions == null || conditions.isEmpty()) {
            throw new IllegalArgumentException("Failed to get Contacts. " + Constants.CONDITIONS_NOT_FOUND);
        }
        try {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put(Constants.SELECT, QUERY_FIELDS);
            queryParams.put(Constants.ORDER_BY, getOrderByQueryParams(queryClauses));

            if (conditions.size() != 1) {
                String filterQuery = getFilterByQueryParams(conditions);
                if (!filterQuery.isBlank()) {
                    queryParams.put(Constants.FILTER, filterQuery);
                }
            }
            return getFilteredContacts(pageIndex, pageSize, queryParams);
        } catch (MalformedURLException cause) {
            throw new RuntimeException(cause.getMessage(), cause.getCause());
        }
    }

    /**
     * Builds OData $filter query parameter from search conditions.
     * Supports filtering by last name, contact ID, and account ID.
     *
     * @param conditions List of SearchCondition objects
     * @return OData filter string (e.g., "contains(lastname, 'Smith') and contactid eq guid")
     */
    private String getFilterByQueryParams(List<SearchCondition> conditions) {
        String lastName = getSearchConditionValue(conditions, LAST_NAME);
        String contactId = getSearchConditionValue(conditions, "id");
        String accountId = getSearchConditionValue(conditions, "accountId");
        String lastNameOperator = getSearchConditionStringOperator(conditions, LAST_NAME);

        List<String> filters = new ArrayList<>();

        if (lastName != null && !lastName.isEmpty()) {
            filters.add(lastNameOperator + "(lastname, '" + lastName + "')");
        }

        if (contactId != null && !contactId.isEmpty()) {
            filters.add("contactid eq " + contactId);
        }

        if (accountId != null && !accountId.isEmpty()) {
            filters.add("_accountid_value eq " + accountId);
        }

        return String.join(" and ", filters);
    }

    /**
     * Retrieves filtered contacts from Dynamics 365 and applies pagination.
     *
     * @param pageIndex Page number (1-based) for pagination
     * @param pageSize Number of results per page
     * @param queryParams OData query parameters ($select, $filter, $orderby)
     * @return List of MsDynamicsContact entities for the requested page
     * @throws MalformedURLException if API endpoint URL is invalid
     */
    private List<MsDynamicsContact> getFilteredContacts(long pageIndex, int pageSize, Map<String, Object> queryParams) throws MalformedURLException {
        List<Contact> allContacts = contactDao.getContactsByQuery(queryParams);
        return allContacts.stream().skip(Math.max((pageIndex - 1) * pageSize, 0)).limit(pageSize)
                .map(CatalogTypes::fromDynamicsToKristaEntity).collect(Collectors.toList());
    }

    /**
     * Builds OData $orderby query parameter from query clauses.
     * Defaults to "contactid DESC" if no clauses provided.
     *
     * @param queryClauses List of QueryClause objects specifying sort order
     * @return OData orderby string (e.g., "contactid desc")
     */
    private String getOrderByQueryParams(List<QueryClause> queryClauses) {
        String orderByField = queryClauses.isEmpty() ? "contactid" : queryClauses.get(0).getFieldName();
        String orderByOperand = queryClauses.isEmpty() ? "DESC" : queryClauses.get(0).getOperand().toString();
        return getSupportedParameterName(orderByField) + " " + orderByOperand.toLowerCase();

    }

    /**
     * Maps catalog field names to Dynamics 365 API field names for ordering.
     *
     * @param orderByField Catalog field name (e.g., "Id", "accountId", "lastName")
     * @return Dynamics 365 API field name (e.g., "contactid", "_parentcustomerid_value", "lastname")
     */
    private String getSupportedParameterName(String orderByField) {
        if (orderByField.equalsIgnoreCase("Id")) {
            return "contactid";
        } else if (orderByField.equalsIgnoreCase("accountId")) {
            return "_parentcustomerid_value";
        } else if (orderByField.equalsIgnoreCase(LAST_NAME)) {
            return "lastname";
        }
        return "_ownerid_value";
    }

    /**
     * Retrieves all contacts associated with a specific company (account) from Dynamics 365.
     * Uses custom relationship endpoint to fetch contacts linked to the account.
     *
     * @param companyId The account GUID to retrieve contacts for
     * @return List of MsDynamicsContact entities associated with the company
     * @throws RuntimeException if API communication fails
     */
    public List<MsDynamicsContact> getContactsByCompanyId(String companyId) {
        LOGGER.info("Getting contacts by company ID: {}", companyId);
        try {
            Map<String, Object> queryParams = new HashMap<>();
            String endpoint = "accounts(" + companyId + ")/new_contact_account";
            final String response = provider.getRestClient().get(endpoint, queryParams);
            JsonArray contactsArray = Constants.GSON.fromJson(response, JsonObject.class).getAsJsonArray(Constants.VALUE);
            List<MsDynamicsContact> contactList = new ArrayList<>();
            for (JsonElement element : contactsArray) {
                JsonObject obj = element.getAsJsonObject();

                MsDynamicsContact contact = new MsDynamicsContact(
                        getAsString(obj, CONTACT_ID_FIELD),
                        getAsString(obj, FIRST_NAME_FIELD),
                        getAsString(obj, LAST_NAME_FIELD),
                        getAsString(obj, PARENT_CUSTOMER_ID_FIELD),
                        getAsString(obj, MOBILE_PHONE_FIELD),
                        getAsString(obj, FAX_FIELD),
                        getAsString(obj, EMAIL_FIELD),
                        getAsString(obj, ADDRESS_LINE1_FIELD),
                        getAsString(obj, ADDRESS_LINE2_FIELD),
                        getAsString(obj, ADDRESS_LINE3_FIELD),
                        getAsString(obj, CITY_FIELD),
                        getAsString(obj, STATE_FIELD),
                        getAsString(obj, POSTAL_CODE_FIELD),
                        getAsString(obj, COUNTRY_FIELD),
                        getAsString(obj, PREFERRED_CONTACT_METHOD_FORMATTED_FIELD)
                );
                contactList.add(contact);
            }

            LOGGER.info("Successfully retrieved {} contacts for company ID: {}", contactList.size(), companyId);
            return contactList;
        } catch (MalformedURLException cause) {
            throw new RuntimeException("Malformed URL while calling Dynamics API", cause);
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

    @Override
    public long count(List<SearchCondition> list) {
        return 0;
    }

    @Override
    public List<String> lookup(Map<String, Object> map, List<String> list) {
        return Collections.emptyList();
    }
}