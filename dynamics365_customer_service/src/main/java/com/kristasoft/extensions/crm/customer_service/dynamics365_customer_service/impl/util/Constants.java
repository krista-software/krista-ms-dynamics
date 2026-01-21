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

package com.kristasoft.extensions.crm.customer_service.dynamics365_customer_service.impl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Constants {

    public static final String INVALID_STATE_PARAMETERS = "Invalid OAuth state parameter received. The authorization request may have expired or been tampered with.";
    public static final String FAILED_TO_GET_CUSTOMER = "Unable to retrieve customer record. Verify the customer ID exists and you have read permissions.";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String HASH = "#";
    public static final String RE_AUTHENTICATION = "Authentication token has expired. Initiating re-authentication flow.";
    public static final String AUTHENTICATION = "No valid authentication found. Initiating authentication flow.";
    public static final String USER_ID = "userId";
    public static final String TEXT = "Text";
    public static final String AUTH_CONTEXT_ID = "authContextId";
    public static final String DEFAULT_SCOPE = "/.default";
    public static final String OFFLINE_ACCESS = "offline_access";
    public static final String ACCOUNTS_API = "accounts";
    public static final String PRIMARY_KEY_IS_EMPTY = "Primary key cannot be empty. Please provide a valid ID.";
    public static final String EMPTY_ENTITY = "Entity data is required. Please provide valid entity information.";
    public static final String ID_IS_NULL_OR_EMPTY = "ID is required. Please provide a valid identifier.";
    public static final String CONDITIONS_NOT_FOUND = "Search conditions are required. Please specify at least one search criterion.";
    public static final String FAILED_TO_SEARCH = "Search operation failed. Verify your search criteria and try again.";
    public static final String REQUIRED_SCOPE = ".default offline_access";
    public static final String STATE = "state";
    public static final String VALUE = "value";
    public static final String AUTHORITY = "https://login.microsoftonline.com/";
    public static final String API_ENDPOINT = "/api/data/v9.2/";
    public static final String FAILED_TO_UPDATE = "Update operation failed. Verify the entity exists and you have update permissions.";
    public static final String INCIDENTS_API = "incidents";
    public static final String CONTACTS_API = "contacts";
    public static final String FAILED_TO_CREATE = "Create operation failed. Verify all required fields are provided and valid.";
    public static final String FAILED_TO_DELETE = "Delete operation failed. Verify the entity exists and you have delete permissions.";
    public static final String CUSTOMER_NAME = "CustomerName";
    public static final String CUSTOMER_ID = "CustomerId";
    public static final String SELECT = "$select";
    public static final String ORDER_BY = "$orderby";
    public static final String FILTER = "$filter";
    public static final String EMPTY_STRING = "";
    public static final String CONTAINS = "contains";
    public static final String AUTHORIZATION_PROMPT = "Authorization required. Please click 'Validate Attributes' to authenticate before saving changes.";

    public static final String CONTACT_ID_FIELD = "contactid";
    public static final String FIRST_NAME_FIELD = "firstname";
    public static final String LAST_NAME_FIELD = "lastname";
    public static final String PARENT_CUSTOMER_ID_FIELD = "_parentcustomerid_value";
    public static final String MOBILE_PHONE_FIELD = "mobilephone";
    public static final String FAX_FIELD = "fax";
    public static final String EMAIL_FIELD = "emailaddress1";
    public static final String ADDRESS_LINE1_FIELD = "address1_line1";
    public static final String ADDRESS_LINE2_FIELD = "address1_line2";
    public static final String ADDRESS_LINE3_FIELD = "address1_line3";
    public static final String CITY_FIELD = "address1_city";
    public static final String STATE_FIELD = "address1_stateorprovince";
    public static final String POSTAL_CODE_FIELD = "address1_postalcode";
    public static final String COUNTRY_FIELD = "address1_country";
    public static final String PREFERRED_CONTACT_METHOD_FORMATTED_FIELD = "preferredcontactmethodcode@OData.Community.Display.V1.FormattedValue";

    public Constants() {
    }
}
