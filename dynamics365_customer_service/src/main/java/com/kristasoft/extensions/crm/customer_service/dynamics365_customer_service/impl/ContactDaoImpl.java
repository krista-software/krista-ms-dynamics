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
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProvider;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.connectors.RestClientProviderFactory;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.model.DynamicsContact;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.Constants;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.Contact;
import com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.service.ContactDao;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util.DynamicsCustomerServiceUtil.getContactMethodFromKey;

@Service
public class ContactDaoImpl implements ContactDao {

    public static final String PREFERRED_CONTACT_METHOD_CODE = "preferredcontactmethodcode";
    public static final String PARENT_CUSTOMER_ID_ACCOUNT_ODATA_BIND = "parentcustomerid_account@odata.bind";
    private final RestClientProvider provider;

    @Inject
    public ContactDaoImpl(RestClientProviderFactory restClientProviderFactory) {
        this(restClientProviderFactory.create());
    }

    public ContactDaoImpl(RestClientProvider provider) {
        this.provider = provider;
    }

    /**
     * Retrieves a single contact by its unique identifier from Dynamics 365.
     *
     * @param id The contact GUID (contactid) to retrieve
     * @return Contact object containing the contact details
     * @throws MalformedURLException if the API endpoint URL is invalid
     * @throws IllegalArgumentException if id is null or blank
     */
    @Override
    public Contact getContactById(String id) throws MalformedURLException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(Constants.PRIMARY_KEY_IS_EMPTY);
        }
        return new ContactImpl(Constants.GSON.fromJson(provider
                        .getRestClient()
                        .get(Constants.CONTACTS_API + "(" + id + ")", new HashMap<>()),
                DynamicsContact.class));
    }

    /**
     * Retrieves all contacts without filtering. Delegates to getContactsByQuery with empty parameters.
     *
     * @param size Pagination size (currently unused, returns all results)
     * @param page Page index (currently unused)
     * @param orderByField Field name for sorting (currently unused)
     * @param orderByOperand Sort direction (currently unused)
     * @return List of all Contact objects
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    @Override
    public List<Contact> getContacts(long size, long page, String orderByField, String orderByOperand)
            throws MalformedURLException {
        return getContactsByQuery(new HashMap<>());
    }

    /**
     * Retrieves contacts based on OData query parameters.
     * Executes GET request to Dynamics 365 contacts API and maps results to Contact objects.
     *
     * @param queryParams Map of OData query parameters (e.g., $filter, $select, $orderby)
     * @return List of Contact objects matching the query, or empty list if no results
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    @Override
    public List<Contact> getContactsByQuery(Map<String, Object> queryParams) throws MalformedURLException {
        String result = provider.getRestClient().get(Constants.CONTACTS_API, queryParams);
        JsonArray jsonArrayResult = Constants.GSON.fromJson(result, JsonObject.class)
                .getAsJsonArray(Constants.VALUE);

        Type listType = new TypeToken<List<DynamicsContact>>() {
        }.getType();
        List<DynamicsContact> contacts = Constants.GSON.fromJson(jsonArrayResult.toString(), listType);

        if (!contacts.isEmpty()) {
            return contacts.stream().map(ContactImpl::new).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Creates a new contact in Dynamics 365.
     * Constructs the payload from entity definition and posts to the contacts API.
     *
     * @param entityDefinition Map containing contact field values
     * @return JSON response string from Dynamics 365 containing the created contact details
     * @throws MalformedURLException if the API endpoint URL is invalid
     * @throws JSONException if JSON construction or parsing fails
     */
    @Override
    public String createContact(Map<String, Object> entityDefinition) throws MalformedURLException, JSONException {
        return provider.getRestClient().post(Constants.CONTACTS_API, getCreateContactPayload(entityDefinition));
    }

    /**
     * Constructs the JSON payload for creating a new contact in Dynamics 365.
     * Maps entity definition fields to Dynamics 365 API field names and handles OData bindings for relationships.
     * Converts contact method from display name to numeric code.
     *
     * @param entityDefinition Map containing contact field values with keys matching catalog entity field names
     * @return JSONObject formatted for Dynamics 365 contact creation API
     * @throws JSONException if JSON construction fails
     */
    private JSONObject getCreateContactPayload(Map<String, Object> entityDefinition) throws JSONException {
        JSONObject target = new JSONObject();
        target.put("firstname", entityDefinition.get("First Name"));
        target.put("lastname", entityDefinition.get("Last Name"));
        String accountIdValue = (String) entityDefinition.get("Account Id");
        if (accountIdValue != null && !accountIdValue.isBlank()) {
            target.put(PARENT_CUSTOMER_ID_ACCOUNT_ODATA_BIND, "/accounts(" + accountIdValue + ")");
        }
        String contactMethodValue = (String) entityDefinition.get("Contact Method");
        if (contactMethodValue != null && !contactMethodValue.isBlank()) {
            target.put(PREFERRED_CONTACT_METHOD_CODE, getContactMethodFromKey(contactMethodValue));
        }
        target.put("mobilephone", entityDefinition.get("Mobile Phone"));
        target.put("fax", entityDefinition.get("Fax"));
        target.put("address1_line1", entityDefinition.get("Street 1"));
        target.put("address1_line2", entityDefinition.get("Street 2"));
        target.put("address1_line3", entityDefinition.get("Street 3"));
        target.put("address1_city", entityDefinition.get("City"));
        target.put("address1_stateorprovince", entityDefinition.get("State"));
        target.put("address1_postalcode", entityDefinition.get("Postal Code"));
        target.put("address1_country", entityDefinition.get("Country"));
        target.put("emailaddress1", entityDefinition.get("Email"));

        return target;
    }

    /**
     * Updates an existing contact in Dynamics 365.
     * Compares old and new contact states, sends only changed fields via PATCH request.
     *
     * @param contact Contact object containing updated field values
     * @return Updated Contact object with latest values from Dynamics 365
     * @throws MalformedURLException if the API endpoint URL is invalid
     * @throws IllegalAccessException if reflection access to fields fails
     */
    @Override
    public Contact updateContact(Contact contact) throws MalformedURLException, IllegalAccessException {
        DynamicsContact updatedContact = DynamicsContact.from(contact);
        DynamicsContact oldContact = DynamicsContact.from(getContactById(contact.getId()));

        Map<String, String> differencesInContacts = getDifference(oldContact, updatedContact);
        return new ContactImpl(Constants.GSON.fromJson(provider
                        .getRestClient()
                        .patch(Constants.CONTACTS_API + "(" + contact.getId() + ")",
                                Constants.GSON.toJson(differencesInContacts)),
                DynamicsContact.class));


    }

    /**
     * Compares two DynamicsContact objects and returns a map of changed fields.
     * Uses reflection to iterate through all fields and identify differences.
     *
     * @param oldContact Original contact state from Dynamics 365
     * @param updatedContact New contact state with changes
     * @return Map of field names to new values for fields that changed (excluding contactid)
     * @throws IllegalAccessException if reflection access to fields fails
     */
    private Map<String, String> getDifference(DynamicsContact oldContact, DynamicsContact updatedContact) throws IllegalAccessException {
        Map<String, String> differences = new HashMap<>();
        Field[] fields = DynamicsContact.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            Object oldValue = field.get(oldContact);
            oldValue = (oldValue == null) ? "" : oldValue;

            Object updatedValue = field.get(updatedContact);
            updatedValue = (updatedValue == null) ? "" : updatedValue;

            if ((!Objects.equals(oldValue, updatedValue)) &&
                    (!field.getName().equalsIgnoreCase("contactid"))) {
                String payloadName = getPayloadName(field);
                if (payloadName != null && !payloadName.isBlank()) {
                    differences.put(payloadName, getPayloadValue(payloadName, (String) updatedValue));
                }
            }
        }
        return differences;
    }

    /**
     * Maps Java field names to Dynamics 365 API payload field names for contacts.
     * Handles special cases for relationship fields and option set fields.
     *
     * @param field The Java Field object to map
     * @return Dynamics 365 API field name or OData binding name
     */
    private String getPayloadName(Field field) {
        SerializedName payloadFieldName = field.getAnnotation(SerializedName.class);

        if (payloadFieldName == null) {
            if (field.getName().equalsIgnoreCase("contactMethod")) {
                return PREFERRED_CONTACT_METHOD_CODE;
            }
        } else if (field.getName().equalsIgnoreCase("accountId")) {
            return PARENT_CUSTOMER_ID_ACCOUNT_ODATA_BIND;
        } else {
            return payloadFieldName.value();
        }
        return "";
    }

    /**
     * Formats field values for Dynamics 365 API payload for contacts.
     * Converts relationship IDs to OData binding format and display values to numeric codes.
     *
     * @param field The field name being updated
     * @param updatedValue The new value for the field
     * @return Formatted value (OData binding for relationships, numeric code for option sets, raw value otherwise)
     */
    private String getPayloadValue(String field, String updatedValue) {
        if (field.equalsIgnoreCase(PREFERRED_CONTACT_METHOD_CODE)) {
            return getContactMethodFromKey(updatedValue);
        } else if (field.equalsIgnoreCase(PARENT_CUSTOMER_ID_ACCOUNT_ODATA_BIND)) {
            return "/accounts(" + updatedValue + ")";
        } else {
            return updatedValue;
        }
    }

    /**
     * Deletes a contact from Dynamics 365 by its unique identifier.
     *
     * @param id The contact GUID (contactid) to delete
     * @throws MalformedURLException if the API endpoint URL is invalid
     */
    @Override
    public void deleteAccountById(String id) throws MalformedURLException {
        provider.getRestClient().delete(Constants.CONTACTS_API + "(" + id + ")");
    }
}